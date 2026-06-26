package com.crosspaste.ui.paste.side

import java.awt.event.KeyEvent.VK_0
import java.awt.event.KeyEvent.VK_1
import java.awt.event.KeyEvent.VK_9
import java.awt.event.KeyEvent.VK_A
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class QuickSlotIndexTest {

    @Test
    fun `number keys map to ten quick slots`() {
        assertEquals(0, quickSlotIndex(VK_1))
        assertEquals(8, quickSlotIndex(VK_9))
        assertEquals(9, quickSlotIndex(VK_0))
    }

    @Test
    fun `non number keys do not map to quick slots`() {
        assertNull(quickSlotIndex(VK_A))
    }
}
