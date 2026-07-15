package com.crosspaste.app

import com.crosspaste.config.AppMetadataRepository
import com.crosspaste.net.SyncApi
import com.crosspaste.utils.getAppEnvUtils
import com.crosspaste.utils.getSystemProperty
import io.github.oshai.kotlinlogging.KotlinLogging
import java.nio.file.Paths
import java.util.Properties

class DesktopAppInfoFactory(
    private val appMetadataRepository: AppMetadataRepository,
) : AppInfoFactory {

    private val logger = KotlinLogging.logger {}

    private val systemProperty = getSystemProperty()

    private val versionProperties: Properties? = loadProperties("crosspaste-version.properties")

    private val buildProperties: Properties? = loadProperties("crosspaste-build.properties")

    private fun loadProperties(resourceName: String): Properties? =
        runCatching {
            val properties = Properties()
            Thread
                .currentThread()
                .contextClassLoader
                .getResourceAsStream(resourceName)
                ?.use { properties.load(it) }
                ?: return@runCatching null
            properties
        }.onFailure { e ->
            logger.warn(e) { "Failed to read $resourceName" }
        }.getOrNull()

    override fun createAppInfo(): AppInfo =
        AppInfo(
            appInstanceId = appMetadataRepository.appInstanceId,
            appVersion = getVersion(),
            appRevision = getRevision(),
            userName = getUserName(),
            pairingVersion = SyncApi.PAIRING_VERSION,
        )

    override fun getVersion(): String = getVersion(appEnvUtils.getCurrentAppEnv(), versionProperties)

    override fun getRevision(): String =
        buildProperties?.getProperty("revision")
            ?: versionProperties?.getProperty("revision", "Unknown")
            ?: "Unknown"

    override fun getUserName(): String {
        val userHome = systemProperty.get("user.home")
        return Paths.get(userHome).toFile().name
    }

    companion object {

        private val appEnvUtils = getAppEnvUtils()

        fun getVersion(
            appEnv: AppEnv,
            properties: Properties?,
        ): String {
            val version = properties?.getProperty("version", "Unknown") ?: return "Unknown"

            return when (appEnv) {
                AppEnv.DEVELOPMENT -> "$version-dev"
                AppEnv.TEST -> "$version-test"
                else -> {
                    val beta =
                        if (appEnv == AppEnv.BETA) {
                            "-beta"
                        } else {
                            ""
                        }
                    properties.getProperty("prerelease")?.let { prerelease ->
                        "$version$beta-$prerelease"
                    } ?: "$version$beta"
                }
            }
        }
    }
}
