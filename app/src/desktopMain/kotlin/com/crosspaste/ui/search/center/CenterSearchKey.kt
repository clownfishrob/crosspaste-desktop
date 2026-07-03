package com.crosspaste.ui.search.center

import androidx.compose.ui.input.key.Key

internal object CenterSearchKey {

    fun isPasteSubmitKey(
        key: Key,
        nativeKeyCode: Int,
    ): Boolean = key == Key.Enter
}
