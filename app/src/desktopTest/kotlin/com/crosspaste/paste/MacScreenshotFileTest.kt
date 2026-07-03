package com.crosspaste.paste

import java.nio.file.attribute.FileTime
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MacScreenshotFileTest {

    @Test
    fun `accepts recent macOS screenshot png names`() {
        val now = 1_000_000L

        assertTrue(
            MacDesktopImageFile.isRecentImage(
                fileName = "Screenshot 2026-07-03 at 09.30.00.png",
                lastModifiedTime = FileTime.fromMillis(now - 1_000),
                nowMillis = now,
            ),
        )
        assertTrue(
            MacDesktopImageFile.isRecentImage(
                fileName = "Screen Shot 2026-07-03 at 09.30.00 AM.png",
                lastModifiedTime = FileTime.fromMillis(now - 1_000),
                nowMillis = now,
            ),
        )
    }

    @Test
    fun `accepts recent desktop image exports`() {
        assertTrue(
            MacDesktopImageFile.isRecentImage(
                fileName = "PasteFlow Dev.png",
                lastModifiedTime = FileTime.fromMillis(1_000),
                nowMillis = 1_100,
            ),
        )
    }

    @Test
    fun `rejects old image files`() {
        assertFalse(
            MacDesktopImageFile.isRecentImage(
                fileName = "Screenshot 2026-07-03 at 09.30.00.png",
                lastModifiedTime = FileTime.fromMillis(1_000),
                nowMillis = 121_001,
            ),
        )
    }

    @Test
    fun `rejects non image files`() {
        assertFalse(
            MacDesktopImageFile.isRecentImage(
                fileName = "notes.txt",
                lastModifiedTime = FileTime.fromMillis(1_000),
                nowMillis = 1_100,
            ),
        )
    }
}
