package com.crosspaste.paste

import com.crosspaste.app.AppFileType
import com.crosspaste.db.paste.PasteDao
import com.crosspaste.db.paste.PasteTagDao
import com.crosspaste.exception.PasteException
import com.crosspaste.exception.StandardErrorCode
import com.crosspaste.notification.MessageType
import com.crosspaste.notification.NotificationManager
import com.crosspaste.paste.item.PasteFiles
import com.crosspaste.paste.item.PasteItemReader
import com.crosspaste.paste.item.bindItem
import com.crosspaste.paste.item.getFilePaths
import com.crosspaste.path.UserDataPathProvider
import com.crosspaste.utils.DateUtils
import com.crosspaste.utils.getCodecsUtils
import com.crosspaste.utils.getCompressUtils
import com.crosspaste.utils.getFileUtils
import com.crosspaste.utils.getJsonUtils
import com.crosspaste.utils.ioDispatcher
import com.crosspaste.utils.namedScope
import com.crosspaste.utils.noOptionParent
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okio.Path

class PasteImportService(
    private val notificationManager: NotificationManager,
    private val pasteDao: PasteDao,
    private val pasteItemReader: PasteItemReader,
    private val pasteTagDao: PasteTagDao,
    private val searchContentService: SearchContentService,
    private val userDataPathProvider: UserDataPathProvider,
) {
    private val logger = KotlinLogging.logger { }

    private val codecsUtils = getCodecsUtils()

    private val compressUtils = getCompressUtils()

    private val fileUtils = getFileUtils()

    private val jsonUtils = getJsonUtils()

    private val ioCoroutineDispatcher = namedScope(ioDispatcher, "PasteImportService")

    private val mutex = Mutex()

    fun import(
        pasteImportParam: PasteImportParam,
        updateProgress: (Float) -> Unit,
    ) {
        ioCoroutineDispatcher.launch {
            mutex.withLock {
                doImport(pasteImportParam, updateProgress)
            }
        }
    }

    private suspend fun doImport(
        pasteImportParam: PasteImportParam,
        updateProgress: (Float) -> Unit,
    ) {
        var importTempPath: Path? = null
        runCatching {
            val tempDir = userDataPathProvider.resolve(appFileType = AppFileType.TEMP)
            val epochMilliseconds = DateUtils.nowEpochMilliseconds()
            val basePath = tempDir.resolve("import-$epochMilliseconds", true)
            importTempPath = basePath
            userDataPathProvider.autoCreateDir(basePath)
            decompress(pasteImportParam, basePath)
            val importCount =
                fileUtils
                    .listFiles(basePath) {
                        it.name.endsWith(".count")
                    }.first()
                    .name
                    .removeSuffix(".count")
                    .toLong()
            val pasteDataFile = basePath.resolve("paste.data")
            if (!fileUtils.existFile(pasteDataFile)) {
                throw PasteException(
                    StandardErrorCode.IMPORT_FAIL.toErrorCode(),
                    "Failed to find paste.data file",
                )
            }

            var totalCount = 0L
            var successCount = 0L
            val importedIds = mutableListOf<Long>()

            fileUtils.readByLines(pasteDataFile) { line ->
                totalCount++
                readPasteData(line)?.let { pasteData ->
                    importPasteData(basePath, totalCount, pasteData)?.let { id ->
                        successCount++
                        importedIds.add(id)
                    }
                } ?: run {
                    logger.error { "Error parsing paste data, index = $totalCount" }
                }
                updateProgress(totalCount.toFloat() / importCount.toFloat())
            }

            restoreCollection(basePath, importedIds)
            if (successCount > 0 && successCount < totalCount) {
                notificationManager.sendNotification(
                    title = { it.getText("import_partial") },
                    messageType = MessageType.Warning,
                )
            } else if (successCount > 0) {
                notificationManager.sendNotification(
                    title = { it.getText("import_successful") },
                    messageType = MessageType.Success,
                )
            } else if (totalCount > 0) {
                notificationManager.sendNotification(
                    title = { it.getText("import_fail") },
                    messageType = MessageType.Error,
                )
            } else {
                notificationManager.sendNotification(
                    title = { it.getText("no_data_import") },
                    messageType = MessageType.Warning,
                )
            }
            updateProgress(1f)
        }.onFailure { e ->
            updateProgress(-1f)
            logger.error(e) { "Error importing paste data" }
            notificationManager.sendNotification(
                title = { it.getText("import_fail") },
                messageType = MessageType.Error,
            )
        }.apply {
            importTempPath?.let { fileUtils.deleteFile(it) }
        }
    }

    // Restore the pinned collection for single-collection exports: the bundle
    // carries the tag metadata in collection.info, and every successfully
    // imported item gets pinned to the (found or recreated) tag.
    private suspend fun restoreCollection(
        basePath: Path,
        importedIds: List<Long>,
    ) {
        if (importedIds.isEmpty()) return
        val infoFile = basePath.resolve(PasteCollectionInfo.COLLECTION_INFO_FILE)
        if (!fileUtils.existFile(infoFile)) return
        runCatching {
            val jsonBuilder = StringBuilder()
            fileUtils.readByLines(infoFile) { line -> jsonBuilder.append(line) }
            val info =
                jsonUtils.JSON.decodeFromString(
                    PasteCollectionInfo.serializer(),
                    jsonBuilder.toString(),
                )
            val tagId =
                pasteTagDao.getAllTagsBlock().firstOrNull { it.name == info.name }?.id
                    ?: pasteTagDao.createPasteTag(info.name, info.color)
            pasteTagDao.addTagsToPastes(importedIds, setOf(tagId))
        }.onFailure { e ->
            logger.error(e) { "Error restoring collection from import" }
        }
    }

    private suspend fun importPasteData(
        basePath: Path,
        index: Long,
        pasteData: PasteData,
    ): Long? {
        var recordId: Long? = null
        return runCatching {
            val id = pasteDao.createPasteData(pasteData)
            recordId = id

            val pasteCoordinate = pasteData.getPasteCoordinate(id = id)
            val pasteAppearItem = pasteData.pasteAppearItem
            val pasteCollection = pasteData.pasteCollection

            val newPasteAppearItem = pasteAppearItem?.bindItem(pasteCoordinate)
            val newPasteCollection = pasteCollection.bindItems(pasteCoordinate)

            val importPasteData =
                pasteData.copy(
                    id = id,
                    pasteAppearItem = newPasteAppearItem,
                    pasteCollection = newPasteCollection,
                    pasteSearchContent =
                        searchContentService.createSearchContent(
                            pasteData.source,
                            newPasteAppearItem?.let { pasteItemReader.getSearchContent(it) },
                        ),
                )

            val pasteFilesList = importPasteData.getPasteAppearItems().filterIsInstance<PasteFiles>()

            if (pasteFilesList.isNotEmpty()) {
                pasteDao.updateFilePath(importPasteData)
            }
            for (pasteFiles in pasteFilesList) {
                moveResource(basePath, index, importPasteData, pasteFiles)
            }

            pasteDao.updatePasteState(id, PasteState.LOADED)
            id
        }.onFailure { e ->
            logger.error(e) { "Error importing paste data, index = $index" }
            recordId?.let { runCatching { pasteDao.updatePasteState(it, PasteState.DELETED) } }
        }.getOrNull()
    }

    private fun moveResource(
        basePath: Path,
        index: Long,
        importPasteData: PasteData,
        pasteFiles: PasteFiles,
    ) {
        val path =
            basePath
                .resolve(importPasteData.appInstanceId)
                .resolve(index.toString())

        for (filePath in pasteFiles.getFilePaths(userDataPathProvider)) {
            val importFilePath = path.resolve(filePath.name)
            userDataPathProvider.autoCreateDir(filePath.noOptionParent)
            fileUtils.moveFile(importFilePath, filePath)
        }
    }

    private fun readPasteData(line: String): PasteData? {
        val json = codecsUtils.base64Decode(line).decodeToString()
        return PasteData.fromJson(json)
    }

    private fun decompress(
        pasteImportParam: PasteImportParam,
        decompressPath: Path,
    ) {
        pasteImportParam.importBufferedSource()?.let { bufferSource ->
            try {
                compressUtils
                    .unzip(bufferSource, decompressPath)
                    .onFailure {
                        logger.error(it) { "Failed to decompress the file" }
                        throw PasteException(
                            errorCode = StandardErrorCode.IMPORT_FAIL.toErrorCode(),
                            message = "Failed to decompress the file",
                        )
                    }
            } finally {
                runCatching { bufferSource.close() }
            }
        } ?: run {
            throw PasteException(
                errorCode = StandardErrorCode.IMPORT_FAIL.toErrorCode(),
                message = "Failed to read the file",
            )
        }
    }
}
