package com.crosspaste.ui.settings

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.composables.icons.materialsymbols.MaterialSymbols
import com.composables.icons.materialsymbols.rounded.Approval
import com.crosspaste.app.DesktopAppLaunchState
import com.crosspaste.config.DesktopConfigManager
import com.crosspaste.i18n.GlobalCopywriter
import com.crosspaste.platform.Platform
import com.crosspaste.platform.macos.MacAppUtils
import com.crosspaste.ui.LocalThemeExtState
import com.crosspaste.ui.base.IconData
import com.crosspaste.ui.base.SectionHeader
import com.crosspaste.ui.base.UISupport
import com.crosspaste.ui.theme.AppUISize.medium
import kotlinx.coroutines.delay
import org.koin.compose.koinInject
import kotlin.time.Duration.Companion.seconds

/**
 * macOS-only "Permissions" settings section: shows the live Accessibility
 * permission state (needed for global shortcuts and paste-back) and jumps to
 * the right System Settings pane when it is missing.
 */
@Composable
fun AccessibilitySettingsContentView() {
    val platform = koinInject<Platform>()
    if (!remember { platform.isMacos() }) {
        return
    }

    val copywriter = koinInject<GlobalCopywriter>()
    val uiSupport = koinInject<UISupport>()

    val themeExt = LocalThemeExtState.current

    // Poll while this screen is composed so the row flips to "granted" without
    // reopening settings once the user enables the permission.
    val granted by produceState(initialValue = MacAppUtils.checkAccessibilityPermissions()) {
        while (true) {
            delay(2.seconds)
            value = MacAppUtils.checkAccessibilityPermissions()
        }
    }

    SectionHeader("permissions", topPadding = medium)

    SettingSectionCard {
        SettingListItem(
            title = "accessibility_permission",
            subtitle = "accessibility_permission_row_desc",
            icon = IconData(MaterialSymbols.Rounded.Approval, themeExt.amberIconColor),
            trailingContent = {
                Text(
                    text =
                        if (granted) {
                            copywriter.getText("granted")
                        } else {
                            copywriter.getText("not_granted")
                        },
                    style = MaterialTheme.typography.labelLarge,
                    color =
                        if (granted) {
                            themeExt.success.color
                        } else {
                            themeExt.warning.color
                        },
                )
            },
            onClick =
                if (granted) {
                    null
                } else {
                    { uiSupport.jumpPrivacyAccessibility() }
                },
        )
    }
}

/**
 * Shows the grant-accessibility onboarding dialog over the main window when
 * the app started without the permission. The dialog polls for the grant and
 * offers a restart once it lands; cancel dismisses it for this session only,
 * and the permissions row in settings remains as the durable entry point.
 */
@Composable
fun AccessibilityDialogHost() {
    val appLaunchState = koinInject<DesktopAppLaunchState>()
    val configManager = koinInject<DesktopConfigManager>()
    val platform = koinInject<Platform>()

    if (!remember { platform.isMacos() }) {
        return
    }

    val config by configManager.config.collectAsState()

    var dismissedThisSession by remember { mutableStateOf(false) }

    if (!appLaunchState.accessibilityPermissions &&
        config.showGrantAccessibility &&
        !dismissedThisSession
    ) {
        GrantAccessibilityDialog {
            dismissedThisSession = true
        }
    }
}
