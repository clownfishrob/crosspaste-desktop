package com.crosspaste.config

import com.crosspaste.clean.CleanTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DesktopAppConfigTest {

    private fun createDefaultConfig(): DesktopAppConfig =
        DesktopAppConfig(
            language = "en",
        )

    @Test
    fun `default config has expected values`() {
        val config = createDefaultConfig()
        assertEquals("en", config.language)
        assertEquals("", config.font)
        assertFalse(config.enableAutoStartUp)
        assertFalse(config.enableDebugMode)
        assertTrue(config.isFollowSystemTheme)
        assertFalse(config.isDarkTheme)
        assertEquals(13139, config.port)
        assertFalse(config.enableEncryptSync)
        assertTrue(config.enableExpirationCleanup)
        assertEquals(CleanTime.TWO_MONTH.ordinal, config.imageCleanTimeIndex)
        assertEquals(CleanTime.TWO_MONTH.ordinal, config.fileCleanTimeIndex)
        assertTrue(config.enableThresholdCleanup)
        assertEquals(2048L, config.maxStorage)
        assertEquals(1000, config.maxHistoryItems)
        assertEquals(20, config.cleanupPercentage)
        assertFalse(config.enableDiscovery)
        assertEquals("[]", config.blacklist)
        assertTrue(config.enablePasteboardListening)
        assertTrue(config.showTutorial)
        assertEquals(20L, config.maxBackupFileSize)
        assertTrue(config.enabledSyncFileSizeLimit)
        assertEquals(20L, config.maxSyncFileSize)
        assertTrue(config.useDefaultStoragePath)
        assertEquals("", config.storagePath)
        assertTrue(config.enableSoundEffect)
        assertFalse(config.legacySoftwareCompatibility)
        assertTrue(config.pastePrimaryTypeOnly)
        assertFalse(config.enableSyncText)
        assertFalse(config.enableSyncUrl)
        assertFalse(config.enableSyncHtml)
        assertFalse(config.enableSyncRtf)
        assertFalse(config.enableSyncImage)
        assertFalse(config.enableSyncFile)
        assertFalse(config.enableSyncColor)
    }

    @Test
    fun `copy with string key updates language`() {
        val config: AppConfig = createDefaultConfig()
        val updated = config.copy("language", "zh")
        assertEquals("zh", updated.language)
        assertEquals(config.port, updated.port)
    }

    @Test
    fun `copy with boolean key updates enableEncryptSync`() {
        val config: AppConfig = createDefaultConfig()
        val updated = config.copy("enableEncryptSync", true)
        assertTrue(updated.enableEncryptSync)
    }

    @Test
    fun `copy with int key updates port`() {
        val config: AppConfig = createDefaultConfig()
        val updated = config.copy("port", 8080)
        assertEquals(8080, updated.port)
    }

    @Test
    fun `copy with long key updates maxStorage`() {
        val config: AppConfig = createDefaultConfig()
        val updated = config.copy("maxStorage", 4096L)
        assertEquals(4096L, updated.maxStorage)
    }

    @Test
    fun `copy with int key updates maxHistoryItems`() {
        val config: AppConfig = createDefaultConfig()
        val updated = config.copy("maxHistoryItems", 500)
        assertEquals(500, updated.maxHistoryItems)
    }

    @Test
    fun `copy with unknown key does not change config`() {
        val config: AppConfig = createDefaultConfig()
        val updated = config.copy("unknownKey", "unknownValue")
        assertEquals(config.language, updated.language)
        assertEquals(config.port, updated.port)
        assertEquals(config.enableEncryptSync, updated.enableEncryptSync)
    }

    @Test
    fun `copy updates all sync content type controls`() {
        val config: AppConfig = createDefaultConfig()
        var updated = config.copy("enableSyncText", true)
        assertTrue(updated.enableSyncText)
        updated = config.copy("enableSyncUrl", true)
        assertTrue(updated.enableSyncUrl)
        updated = config.copy("enableSyncHtml", true)
        assertTrue(updated.enableSyncHtml)
        updated = config.copy("enableSyncRtf", true)
        assertTrue(updated.enableSyncRtf)
        updated = config.copy("enableSyncImage", true)
        assertTrue(updated.enableSyncImage)
        updated = config.copy("enableSyncFile", true)
        assertTrue(updated.enableSyncFile)
        updated = config.copy("enableSyncColor", true)
        assertTrue(updated.enableSyncColor)
    }

    @Test
    fun `copy chain updates multiple fields`() {
        val config: AppConfig = createDefaultConfig()
        val updated =
            config
                .copy("port", 9999)
                .copy("language", "de")
                .copy("enableEncryptSync", true)
        assertEquals(9999, updated.port)
        assertEquals("de", updated.language)
        assertTrue(updated.enableEncryptSync)
    }
}
