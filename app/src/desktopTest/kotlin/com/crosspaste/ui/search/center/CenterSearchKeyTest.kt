package com.crosspaste.ui.search.center

import androidx.compose.ui.input.key.Key
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CenterSearchKeyTest {

    @Test
    fun `recognises enter as paste submit key`() {
        assertTrue(CenterSearchKey.isPasteSubmitKey(Key.Enter, nativeKeyCode = 0))
        assertTrue(CenterSearchKey.isPasteSubmitKey(Key.Enter, nativeKeyCode = 10))
        assertFalse(CenterSearchKey.isPasteSubmitKey(Key.Unknown, nativeKeyCode = 10))
        assertFalse(CenterSearchKey.isPasteSubmitKey(Key.Unknown, nativeKeyCode = 52))
        assertFalse(CenterSearchKey.isPasteSubmitKey(Key.DirectionDown, nativeKeyCode = 0))
    }
}
