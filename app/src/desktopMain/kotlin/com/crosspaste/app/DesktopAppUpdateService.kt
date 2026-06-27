package com.crosspaste.app

import io.github.z4kn4fein.semver.Version
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * PasteFlow Dev does not have an update channel yet.
 *
 * Keep this service inert so MVP builds do not periodically contact upstream
 * CrossPaste update infrastructure while the product direction is still being
 * decided. The legacy updater classes remain in the tree for a later rebrand.
 */
class DesktopAppUpdateService(
    appInfo: AppInfo,
) : AppUpdateService {

    private val _currentVersion: MutableStateFlow<Version> =
        MutableStateFlow(
            Version.parse(appInfo.appVersion),
        )

    override val currentVersion: StateFlow<Version> = _currentVersion

    private val _lastVersion: MutableStateFlow<Version?> = MutableStateFlow(null)

    override val lastVersion: StateFlow<Version?> = _lastVersion

    private val noNewVersion: MutableStateFlow<Boolean> = MutableStateFlow(false)

    override suspend fun checkForUpdate() {
        _lastVersion.value = null
    }

    override fun existNewVersion(): Flow<Boolean> = noNewVersion

    override fun start() = Unit

    override fun stop() = Unit

    override fun tryTriggerUpdate() = Unit
}
