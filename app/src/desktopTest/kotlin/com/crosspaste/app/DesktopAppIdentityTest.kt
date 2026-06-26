package com.crosspaste.app

import java.util.Properties
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.fail

class DesktopAppIdentityTest {

    @Test
    fun `development identity does not reuse CrossPaste identifiers`() {
        assertEquals("PasteFlow Dev", DesktopAppIdentity.displayName)
        assertEquals("com.robdev.pasteflow.dev", DesktopAppIdentity.bundleId)
        assertEquals("com.robdev.pasteflow.dev.desktop", DesktopAppIdentity.nativeMessagingHostName)
        assertEquals("_pasteflowDevService._tcp.local.", DesktopAppIdentity.bonjourServiceType)
        assertEquals("pasteflow-dev", DesktopAppIdentity.bonjourServiceNamePrefix)
        assertEquals(13139, DesktopAppIdentity.defaultPort)
        assertEquals(".pasteflow-dev", DesktopAppIdentity.devDataDir)
    }

    @Test
    fun `development shortcut defaults do not reuse CrossPaste shortcuts`() {
        listOf("Linux", "Macos", "Windows").forEach { platform ->
            val properties = shortcutProperties(platform)

            assertEquals(DesktopAppIdentity.defaultMainShortcut, properties.getProperty("show_main"))
            assertEquals(DesktopAppIdentity.defaultSearchShortcut, properties.getProperty("show_search"))
            assertNotEquals("42+3675+51", properties.getProperty("show_main"))
            assertNotEquals("42+3675+57", properties.getProperty("show_search"))
        }
    }

    @Test
    fun `clipboard manager limits are explicit`() {
        assertEquals(2L * 1024L * 1024L, DesktopAppIdentity.maxTextBytes)
        assertEquals(20L * 1024L * 1024L, DesktopAppIdentity.maxImageBytes)
    }

    private fun shortcutProperties(platform: String): Properties =
        Properties().apply {
            val resourceName = "shortcut_keys/$platform.properties"
            val classLoader =
                Thread.currentThread().contextClassLoader
                    ?: ClassLoader.getSystemClassLoader()
            val inputStream =
                classLoader.getResourceAsStream(resourceName)
                    ?: fail("Missing shortcut resource: $resourceName")
            inputStream.use(::load)
        }
}
