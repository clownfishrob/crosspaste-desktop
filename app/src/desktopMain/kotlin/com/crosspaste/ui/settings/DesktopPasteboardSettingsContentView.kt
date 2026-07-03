package com.crosspaste.ui.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.composables.icons.materialsymbols.MaterialSymbols
import com.composables.icons.materialsymbols.rounded.Shield
import com.crosspaste.config.DesktopConfigManager
import com.crosspaste.platform.Platform
import com.crosspaste.ui.LocalThemeExtState
import com.crosspaste.ui.base.IconData
import com.crosspaste.ui.theme.AppUISize.xxxxLarge
import org.koin.compose.koinInject

@Composable
fun DesktopPasteboardSettingsContentView(platform: Platform) {
    SecretDetectionSettingView()

    val isWindows = remember { platform.isWindows() }
    if (isWindows) {
        WindowsPasteboardSettingsContentView()
    }
}

@Composable
private fun SecretDetectionSettingView() {
    val configManager = koinInject<DesktopConfigManager>()
    val themeExt = LocalThemeExtState.current

    val config by configManager.config.collectAsState()

    HorizontalDivider(modifier = Modifier.padding(start = xxxxLarge))

    SettingListSwitchItem(
        title = "secret_detection",
        subtitle = "secret_detection_desc",
        icon = IconData(MaterialSymbols.Rounded.Shield, themeExt.amberIconColor),
        checked = config.enableSecretDetection,
    ) { newEnableSecretDetection ->
        configManager.updateConfig("enableSecretDetection", newEnableSecretDetection)
    }
}
