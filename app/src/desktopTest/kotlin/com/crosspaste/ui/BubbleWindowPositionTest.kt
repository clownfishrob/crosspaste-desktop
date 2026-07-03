package com.crosspaste.ui

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

class BubbleWindowPositionTest {

    @Test
    fun `center search bubble anchors to fixed list column`() {
        val placement =
            BubbleWindowPosition.calculate(
                searchX = 100.dp,
                searchY = 600.dp,
                searchWidth = 780.dp,
                windowWidth = 480.dp,
                windowHeight = 372.dp,
                gap = 4.dp,
                targetCenterXInSearchWindow = 150.dp,
                verticalOffset = 48.dp,
            )

        assertEquals(100.dp, placement.x)
        assertEquals(272.dp, placement.y)
        assertEquals(0.3125f, placement.tailCenterFraction)
    }

    @Test
    fun `center search anchor ignores vertical item scroll offset`() {
        assertEquals(
            150.dp,
            BubbleWindowPosition.centerSearchListAnchorX(centerSearchListWidth = 300.dp),
        )
    }
}
