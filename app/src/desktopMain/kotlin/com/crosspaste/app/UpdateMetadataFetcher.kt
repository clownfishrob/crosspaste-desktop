package com.crosspaste.app

import com.crosspaste.net.ResourcesClient
import com.crosspaste.utils.getJsonUtils
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.Serializable
import java.io.StringReader
import java.util.Properties

/** Latest-release coordinates shared by the update banner and the portable-zip downloader. */
data class ReleaseMetadata(
    val version: String,
    val revision: String,
    val tag: String,
)

/**
 * Shape of a desktop version JSON endpoint. [tag] is optional so a
 * payload without it still resolves (we derive `version.revision`, the same tag
 * GitHub releases use).
 */
@Serializable
private data class DesktopVersionApi(
    val version: String,
    val revision: String,
    val tag: String? = null,
)

/**
 * Resolves the latest published release, preferring release `metadata.properties`
 * and optionally falling back to a JSON version endpoint.
 *
 * PasteFlow Dev currently keeps update delivery paused; this helper remains only
 * as retained infrastructure for a future owned release channel.
 */
class UpdateMetadataFetcher(
    private val resourcesClient: ResourcesClient,
) {
    private val logger = KotlinLogging.logger {}

    /**
     * @param metadataPropertiesUrl GitHub-style `metadata.properties` (also the
     *   test-override target); tried first.
     * @param versionApiUrl JSON version endpoint, tried only when the first source
     *   fails. Pass null to disable the fallback (e.g. under a test override, so
     *   the test source stays the single source of truth).
     */
    suspend fun fetchLatest(
        metadataPropertiesUrl: String,
        versionApiUrl: String?,
    ): ReleaseMetadata? =
        fetchFromMetadataProperties(metadataPropertiesUrl)
            ?: versionApiUrl?.let { fetchFromVersionApi(it) }

    private suspend fun fetchFromMetadataProperties(url: String): ReleaseMetadata? =
        resourcesClient.request(url).getOrNull()?.let { response ->
            runCatching {
                val properties = Properties()
                properties.load(StringReader(response.getBodyAsText()))
                val version = properties.getProperty("app.version") ?: return@runCatching null
                val revision = properties.getProperty("app.revision") ?: return@runCatching null
                ReleaseMetadata(version, revision, "$version.$revision")
            }.onFailure { logger.warn(it) { "Failed to read metadata.properties from $url" } }
                .getOrNull()
        }

    private suspend fun fetchFromVersionApi(url: String): ReleaseMetadata? =
        resourcesClient.request(url).getOrNull()?.let { response ->
            runCatching {
                val api =
                    getJsonUtils().JSON.decodeFromString<DesktopVersionApi>(response.getBodyAsText())
                ReleaseMetadata(api.version, api.revision, api.tag ?: "${api.version}.${api.revision}")
            }.onFailure { logger.warn(it) { "Failed to read desktop.json from $url" } }
                .getOrNull()
        }
}
