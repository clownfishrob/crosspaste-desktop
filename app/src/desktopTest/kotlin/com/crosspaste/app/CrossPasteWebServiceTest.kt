package com.crosspaste.app

import com.crosspaste.net.ClientResponse
import com.crosspaste.net.DownloadProgressListener
import com.crosspaste.net.ResourcesClient
import okio.Path
import kotlin.test.Test
import kotlin.test.assertEquals

class CrossPasteWebServiceTest {

    @Test
    fun `repository home url is not treated as localized website`() {
        val service =
            CrossPasteWebService(
                appUrls =
                    object : AppUrls {
                        override val homeUrl = "https://github.com/clownfishrob/crosspaste-desktop"
                        override val changeLogUrl = "$homeUrl/blob/pasteflow-dev-mvp/CHANGELOG.md"
                        override val checkMetadataUrl = ""
                        override val issueTrackerUrl = "$homeUrl/issues"
                    },
                resourcesClient = NoopResourcesClient,
            )

        assertEquals(
            "https://github.com/clownfishrob/crosspaste-desktop",
            service.getWebUrl("en", "download"),
        )
    }

    @Test
    fun `website home url still resolves localized path`() {
        val service =
            CrossPasteWebService(
                appUrls =
                    object : AppUrls {
                        override val homeUrl = "https://example.test"
                        override val changeLogUrl = "$homeUrl/changelog"
                        override val checkMetadataUrl = ""
                        override val issueTrackerUrl = "$homeUrl/issues"
                    },
                resourcesClient = NoopResourcesClient,
            )

        assertEquals("https://example.test/en/download", service.getWebUrl("en", "download"))
    }

    private object NoopResourcesClient : ResourcesClient {
        override suspend fun request(url: String): Result<ClientResponse> = Result.failure(NotImplementedError())

        override suspend fun download(
            url: String,
            path: Path,
            listener: DownloadProgressListener,
        ) {
            error("Not used by this test")
        }
    }
}
