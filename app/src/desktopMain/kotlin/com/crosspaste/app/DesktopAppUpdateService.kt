package com.crosspaste.app

import com.crosspaste.notification.MessageType
import com.crosspaste.notification.NotificationManager
import com.crosspaste.ui.base.UISupport
import com.crosspaste.utils.ioDispatcher
import com.crosspaste.utils.namedScope
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.z4kn4fein.semver.Version
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

private const val MICROSOFT_STORE_URI = "ms-windows-store://pdp?productId=9P6X7D7DMCCR"

/**
 * PasteFlow Dev uses a conservative update channel for the MVP: the app can
 * detect when a newer build exists and send the user to the project releases,
 * but macOS self-replacement stays manual until release signing is decided.
 */
class DesktopAppUpdateService(
    appInfo: AppInfo,
    private val appUrls: AppUrls,
    private val uiSupport: UISupport,
    private val notificationManager: NotificationManager,
    private val metadataFetcher: UpdateMetadataFetcher,
    private val windowsZipUpdater: WindowsZipUpdater,
    private val appWindowManager: DesktopAppWindowManager,
) : AppUpdateService {

    private val logger = KotlinLogging.logger {}

    private val coroutineScope = namedScope(ioDispatcher, "DesktopAppUpdateService")

    private val _currentVersion: MutableStateFlow<Version> =
        MutableStateFlow(
            Version.parse(appInfo.appVersion),
        )

    override val currentVersion: StateFlow<Version> = _currentVersion

    private val _lastVersion: MutableStateFlow<Version?> = MutableStateFlow(null)

    override val lastVersion: StateFlow<Version?> = _lastVersion

    private var checkUpdate: Job? = null

    private fun startPeriodicUpdateCheck(): Job =
        coroutineScope.launch {
            while (true) {
                checkForUpdate()
                delay(TimeUnit.HOURS.toMillis(2))
            }
        }

    override suspend fun checkForUpdate() {
        _lastVersion.value = readLastVersion()
    }

    override fun existNewVersion(): Flow<Boolean> =
        combine(currentVersion, lastVersion) { current, last ->
            last?.let { it > current } == true
        }

    override fun start() {
        checkUpdate = startPeriodicUpdateCheck()
    }

    override fun stop() {
        checkUpdate?.cancel()
    }

    override fun tryTriggerUpdate() {
        coroutineScope.launch {
            checkForUpdate()
            val hasNewVersion = lastVersion.value?.let { it > currentVersion.value } ?: false

            if (!hasNewVersion) {
                notificationManager.sendNotification(
                    title = { it.getText("no_new_version_available") },
                    messageType = MessageType.Info,
                )
                return@launch
            }

            notificationManager.sendNotification(
                title = { it.getText("new_version_available") },
                messageType = MessageType.Info,
            )

            when (windowsZipUpdater.channel) {
                WindowsUpdateChannel.STORE -> openMicrosoftStore()
                WindowsUpdateChannel.PORTABLE_ZIP -> {
                    windowsZipUpdater.resetUpdatePrompt()
                    appWindowManager.showMainWindow(WindowTrigger.MENU)
                }
                else -> openReleasePage()
            }
        }
    }

    private fun openReleasePage() {
        val releaseUrl = "${appUrls.homeUrl.trimEnd('/')}/releases"
        uiSupport.openUrlInBrowser(releaseUrl)
    }

    private fun openMicrosoftStore() {
        runCatching {
            ProcessBuilder(listOf("cmd", "/c", "start", "", MICROSOFT_STORE_URI)).start()
        }.onFailure { e ->
            logger.error(e) { "Failed to open Microsoft Store" }
            openReleasePage()
        }
    }

    private suspend fun readLastVersion(): Version? {
        val metadataUrl = windowsZipUpdater.overrideMetadataUrl ?: appUrls.checkMetadataUrl
        if (metadataUrl.isBlank()) {
            return null
        }

        val versionApiUrl =
            if (windowsZipUpdater.overrideMetadataUrl != null) {
                null
            } else {
                DesktopAppUrls.versionApiUrl.takeIf { it.isNotBlank() }
            }

        return metadataFetcher
            .fetchLatest(
                metadataPropertiesUrl = metadataUrl,
                versionApiUrl = versionApiUrl,
            )?.let { Version.parse(it.version) }
    }
}
