package com.crosspaste.ui.paste.side.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.crosspaste.ui.theme.AppUIColors
import com.crosspaste.ui.theme.AppUISize.medium
import com.crosspaste.ui.theme.AppUISize.tiny
import com.crosspaste.ui.theme.AppUISize.tiny4XRoundedCornerShape
import com.crosspaste.ui.theme.AppUISize.xxLarge
import com.crosspaste.ui.theme.DesktopAppUIFont.keyboardCharTextStyle

@Composable
fun QuickSlotIndexView(
    index: Int,
    active: Boolean = true,
) {
    // Active (Ctrl held) uses the accent color; idle uses a quiet chip so the
    // slot number stays discoverable without competing with the preview.
    val background =
        if (active) {
            AppUIColors.importantColor
        } else {
            MaterialTheme.colorScheme.surfaceContainerHighest
        }
    val textColor =
        if (active) {
            MaterialTheme.colorScheme.contentColorFor(AppUIColors.importantColor)
        } else {
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        }
    Row(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(start = medium, bottom = tiny),
        verticalAlignment = Alignment.Bottom,
    ) {
        Box(
            modifier =
                Modifier
                    .size(xxLarge, medium)
                    .clip(tiny4XRoundedCornerShape)
                    .background(background),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "#${index + 1}",
                style = keyboardCharTextStyle,
                color = textColor,
            )
        }
    }
}
