package com.crosspaste.paste

import com.crosspaste.platform.Platform
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds.ENTRY_CREATE
import java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY
import java.nio.file.WatchService
import java.nio.file.attribute.FileTime
import kotlin.io.path.exists
import kotlin.io.path.isRegularFile
import kotlin.time.Duration.Companion.milliseconds

object MacDesktopImageFile {

    private val imageExtensions = setOf("png", "jpg", "jpeg", "heic")

    fun isRecentImage(
        fileName: String,
        lastModifiedTime: FileTime,
        nowMillis: Long = System.currentTimeMillis(),
    ): Boolean {
        val extension = fileName.substringAfterLast('.', missingDelimiterValue = "").lowercase()
        if (extension !in imageExtensions) return false
        return nowMillis - lastModifiedTime.toMillis() <= 120_000L
    }
}

class MacScreenshotFileMonitor(
    private val platform: Platform,
    private val pasteConsumer: TransferableConsumer,
    private val desktopPath: Path = Path.of(System.getProperty("user.home"), "Desktop"),
) {

    private val logger = KotlinLogging.logger {}
    private val scope = CoroutineScope(SupervisorJob())

    private var watchService: WatchService? = null
    private var watchJob: Job? = null
    private val pending = mutableSetOf<Path>()

    @Synchronized
    fun start() {
        if (!platform.isMacos() || watchJob?.isActive == true || !desktopPath.exists()) return

        watchService = FileSystems.getDefault().newWatchService()
        desktopPath.register(watchService, ENTRY_CREATE, ENTRY_MODIFY)
        watchJob =
            scope.launch {
                watchLoop()
            }
        logger.info { "Started macOS desktop image file monitor for $desktopPath" }
    }

    @Synchronized
    fun stop() {
        watchJob?.cancel()
        watchJob = null
        runCatching { watchService?.close() }
        watchService = null
        synchronized(pending) {
            pending.clear()
        }
    }

    private suspend fun watchLoop() {
        while (kotlin.coroutines.coroutineContext.isActive) {
            val key =
                runCatching {
                    watchService?.take()
                }.getOrNull() ?: break

            for (event in key.pollEvents()) {
                val context = event.context() as? Path ?: continue
                val path = desktopPath.resolve(context)
                if (event.kind() == ENTRY_CREATE || event.kind() == ENTRY_MODIFY) {
                    maybeCapture(path)
                }
            }

            if (!key.reset()) break
        }
    }

    private fun maybeCapture(path: Path) {
        if (!path.isRegularFile()) return
        val fileName = path.fileName.toString()
        val modifiedTime = runCatching { Files.getLastModifiedTime(path) }.getOrNull() ?: return
        if (!MacDesktopImageFile.isRecentImage(fileName, modifiedTime)) return

        synchronized(pending) {
            if (!pending.add(path)) return
        }

        scope.launch {
            try {
                if (waitForStableFile(path)) {
                    pasteConsumer.consume(
                        DesktopReadTransferable(FileListTransferable(listOf(path.toFile()))),
                        PasteSourceContext(source = "Desktop image", remote = false),
                    )
                }
            } finally {
                synchronized(pending) {
                    pending.remove(path)
                }
            }
        }
    }

    private suspend fun waitForStableFile(path: Path): Boolean {
        var previousSize = -1L
        repeat(20) {
            val currentSize = runCatching { Files.size(path) }.getOrDefault(-1L)
            if (currentSize > 0 && currentSize == previousSize) {
                return true
            }
            previousSize = currentSize
            delay(100.milliseconds)
        }
        return false
    }

    private class FileListTransferable(
        private val files: List<File>,
    ) : Transferable {

        override fun getTransferDataFlavors(): Array<DataFlavor> = arrayOf(DataFlavor.javaFileListFlavor)

        override fun isDataFlavorSupported(flavor: DataFlavor): Boolean = flavor == DataFlavor.javaFileListFlavor

        override fun getTransferData(flavor: DataFlavor): Any {
            if (!isDataFlavorSupported(flavor)) {
                throw java.awt.datatransfer.UnsupportedFlavorException(flavor)
            }
            return files
        }
    }
}
