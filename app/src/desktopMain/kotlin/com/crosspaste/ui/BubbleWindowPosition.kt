package com.crosspaste.ui

import androidx.compose.ui.unit.Dp

internal object BubbleWindowPosition {

    data class Placement(
        val x: Dp,
        val y: Dp,
        val tailCenterFraction: Float,
    )

    fun centerSearchListAnchorX(centerSearchListWidth: Dp): Dp = centerSearchListWidth / 2

    fun calculate(
        searchX: Dp,
        searchY: Dp,
        searchWidth: Dp,
        windowWidth: Dp,
        windowHeight: Dp,
        gap: Dp,
        targetCenterXInSearchWindow: Dp?,
        verticalOffset: Dp,
    ): Placement {
        val idealBubbleX =
            if (targetCenterXInSearchWindow != null) {
                searchX + targetCenterXInSearchWindow - windowWidth / 2
            } else {
                searchX + (searchWidth - windowWidth) / 2
            }

        val minX = searchX
        val maxX = searchX + searchWidth - windowWidth
        val clampedBubbleX = idealBubbleX.coerceIn(minX, maxX)

        val tailCenterFraction =
            if (targetCenterXInSearchWindow != null) {
                val itemScreenX = searchX + targetCenterXInSearchWindow
                ((itemScreenX - clampedBubbleX) / windowWidth).coerceIn(0.1f, 0.9f)
            } else {
                0.5f
            }

        return Placement(
            x = clampedBubbleX,
            y = searchY - windowHeight - gap + verticalOffset,
            tailCenterFraction = tailCenterFraction,
        )
    }
}
