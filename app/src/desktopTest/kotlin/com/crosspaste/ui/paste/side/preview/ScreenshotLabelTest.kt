package com.crosspaste.ui.paste.side.preview

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ScreenshotLabelTest {

    @Test
    fun `labels long screenshots by image dimensions`() {
        assertEquals(
            "long_screenshot",
            ScreenshotLabel.getLabelKey(
                width = 1000,
                height = 4000,
                fileName = "capture.png",
            ),
        )
    }

    @Test
    fun `labels screenshots by common screenshot file names`() {
        assertEquals(
            "screenshot",
            ScreenshotLabel.getLabelKey(
                width = 1440,
                height = 900,
                fileName = "Screenshot 2026-06-26 at 10.00.00.png",
            ),
        )
    }

    @Test
    fun `does not label ordinary images`() {
        assertNull(
            ScreenshotLabel.getLabelKey(
                width = 640,
                height = 480,
                fileName = "avatar.png",
            ),
        )
    }
}
