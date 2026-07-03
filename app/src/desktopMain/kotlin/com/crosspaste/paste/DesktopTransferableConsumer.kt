package com.crosspaste.paste

import com.crosspaste.app.AppInfo
import com.crosspaste.config.DesktopConfigManager
import com.crosspaste.db.paste.PasteDao
import com.crosspaste.notification.MessageType
import com.crosspaste.notification.NotificationManager
import com.crosspaste.paste.plugin.type.PasteTypePlugin
import com.crosspaste.utils.LoggerExtension.logSuspendExecutionTime
import io.github.oshai.kotlinlogging.KotlinLogging
import java.awt.datatransfer.DataFlavor

class DesktopTransferableConsumer(
    private val appInfo: AppInfo,
    private val configManager: DesktopConfigManager,
    private val notificationManager: NotificationManager,
    private val pasteDao: PasteDao,
    private val pasteReleaseService: PasteReleaseService,
    pasteTypePlugins: List<PasteTypePlugin>,
) : TransferableConsumer {

    override val logger = KotlinLogging.logger {}

    private val pasteTypePluginMap: Map<String, PasteTypePlugin> =
        pasteTypePlugins
            .flatMap { pasteTypePlugin ->
                pasteTypePlugin.getIdentifiers().map { it to pasteTypePlugin }
            }.toMap()

    private fun createDataFlavorMap(pasteTransferable: PasteTransferable): Map<String, List<PasteDataFlavor>> {
        val dataFlavorMap = LinkedHashMap<String, MutableList<PasteDataFlavor>>()
        pasteTransferable as DesktopReadTransferable
        for (flavor in pasteTransferable.transferable.transferDataFlavors) {
            val humanPresentableName = flavor.humanPresentableName
            if (!dataFlavorMap.containsKey(humanPresentableName)) {
                dataFlavorMap[humanPresentableName] = mutableListOf()
            }
            dataFlavorMap[humanPresentableName]?.add(flavor.toPasteDataFlavor())
        }
        return dataFlavorMap
    }

    override suspend fun consume(
        pasteTransferable: PasteTransferable,
        sourceContext: PasteSourceContext,
    ): Result<Unit> {
        return runCatching {
            logSuspendExecutionTime(logger, "consume") {
                val dataFlavorMap: Map<String, List<PasteDataFlavor>> = createDataFlavorMap(pasteTransferable)

                dataFlavorMap[LocalOnlyFlavor.humanPresentableName]?.let {
                    logger.info { "Ignoring local only flavor" }
                    return@logSuspendExecutionTime
                }

                if (configManager.getCurrentConfig().enableSecretDetection &&
                    isLikelySecret(pasteTransferable)
                ) {
                    logger.info { "Skipping capture of likely secret content" }
                    notificationManager.sendNotification(
                        title = { it.getText("secret_skipped") },
                        message = { it.getText("secret_skipped_desc") },
                        messageType = MessageType.Warning,
                    )
                    return@logSuspendExecutionTime
                }

                val pasteCollector =
                    PasteCollector(
                        dataFlavorMap.size,
                        appInfo,
                        pasteDao,
                        pasteReleaseService,
                        sourceContext,
                    )

                preCollect(dataFlavorMap, pasteTransferable, pasteCollector)
                pasteCollector.createPrePasteData()?.also { id ->
                    updatePasteData(id, dataFlavorMap, pasteTransferable, pasteCollector)
                    pasteCollector.completeCollect(id)
                }
            }
        }.onFailure { e ->
            logger.error(e) { "Failed to consume transferable" }
        }
    }

    private fun isLikelySecret(pasteTransferable: PasteTransferable): Boolean =
        runCatching {
            val transferable = (pasteTransferable as DesktopReadTransferable).transferable
            transferable
                .takeIf { it.isDataFlavorSupported(DataFlavor.stringFlavor) }
                ?.getTransferData(DataFlavor.stringFlavor)
                ?.let { it as? String }
                ?.let { SecretDetector.isLikelySecret(it) }
                ?: false
        }.getOrDefault(false)

    override fun getPlugin(identity: String): PasteTypePlugin? = pasteTypePluginMap[identity]
}
