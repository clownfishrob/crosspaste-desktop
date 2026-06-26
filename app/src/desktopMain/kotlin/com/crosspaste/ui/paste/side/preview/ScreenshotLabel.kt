package com.crosspaste.ui.paste.side.preview

object ScreenshotLabel {

    fun getLabelKey(
        width: Int?,
        height: Int?,
        fileName: String,
    ): String? {
        if (width != null && height != null && width > 0 && height > 0) {
            if (height >= 2000 && height >= width * 3) {
                return "long_screenshot"
            }
        }

        val normalized = fileName.lowercase()
        return if (
            normalized.contains("screenshot") ||
            normalized.contains("screen shot") ||
            normalized.contains("screen-shot")
        ) {
            "screenshot"
        } else {
            null
        }
    }
}
