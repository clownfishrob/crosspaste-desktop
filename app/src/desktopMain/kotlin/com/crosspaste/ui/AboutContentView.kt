package com.crosspaste.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.fontawesome.FontAwesome
import com.composables.icons.fontawesome.brands.Github
import com.composables.icons.materialsymbols.MaterialSymbols
import com.composables.icons.materialsymbols.rounded.Auto_awesome
import com.composables.icons.materialsymbols.rounded.Feedback
import com.composables.icons.materialsymbols.rounded.Language
import com.composables.icons.materialsymbols.rounded.Mail
import com.composables.icons.materialsymbols.rounded.School
import com.crosspaste.app.AppInfo
import com.crosspaste.app.AppName
import com.crosspaste.app.AppUrls
import com.crosspaste.config.DesktopConfigManager
import com.crosspaste.i18n.GlobalCopywriter
import com.crosspaste.ui.base.CrossPasteLogoView
import com.crosspaste.ui.base.IconData
import com.crosspaste.ui.base.UISupport
import com.crosspaste.ui.settings.SettingListItem
import com.crosspaste.ui.settings.SettingSectionCard
import com.crosspaste.ui.theme.AppUISize.giant
import com.crosspaste.ui.theme.AppUISize.medium
import com.crosspaste.ui.theme.AppUISize.small
import com.crosspaste.ui.theme.AppUISize.tiny
import com.crosspaste.ui.theme.AppUISize.xxLarge
import com.crosspaste.ui.theme.AppUISize.xxxxLarge
import org.koin.compose.koinInject

private const val PROJECT_REPOSITORY_URL = "https://github.com/clownfishrob/crosspaste-desktop"
private const val PROJECT_README_URL = "$PROJECT_REPOSITORY_URL/blob/pasteflow-dev-mvp/README.md"
private const val PROJECT_LICENSE_URL = "$PROJECT_REPOSITORY_URL/blob/pasteflow-dev-mvp/LICENSE"

@Composable
fun AboutContentView() {
    val appInfo = koinInject<AppInfo>()
    val appUrls = koinInject<AppUrls>()
    val uiSupport = koinInject<UISupport>()
    val configManager = koinInject<DesktopConfigManager>()
    val copywriter = koinInject<GlobalCopywriter>()
    val navigationManager = koinInject<NavigationManager>()
    val themeExt = LocalThemeExtState.current

    val config by configManager.config.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Brand Section
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            CrossPasteLogoView(
                size = giant,
                color = MaterialTheme.colorScheme.primary,
                enableDebugToggle = true,
            )

            Spacer(modifier = Modifier.height(medium))

            Text(
                text =
                    if (!config.enableDebugMode) {
                        AppName
                    } else {
                        "$AppName [Debug]"
                    },
                style =
                    MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.sp,
                    ),
                color = MaterialTheme.colorScheme.onSurface,
            )

            Text(
                modifier = Modifier.padding(top = tiny),
                text = "Version ${appInfo.displayVersion()}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            // Social Links
            Row(
                modifier = Modifier.padding(top = medium),
                horizontalArrangement = Arrangement.spacedBy(medium),
            ) {
                SocialLinkButton(
                    icon = FontAwesome.Brands.Github,
                    onClick = { uiSupport.openUrlInBrowser(PROJECT_REPOSITORY_URL) },
                )
                SocialLinkButton(
                    icon = MaterialSymbols.Rounded.Language,
                    onClick = { uiSupport.openUrlInBrowser(PROJECT_README_URL) },
                )
            }
        }

        // Resources Section
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(small),
        ) {
            Text(
                text = copywriter.getText("resources"),
                style =
                    MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            SettingSectionCard {
                SettingListItem(
                    title = "project_repository",
                    subtitle = "project_repository_desc",
                    icon = IconData(MaterialSymbols.Rounded.Language, themeExt.blueIconColor),
                ) {
                    uiSupport.openUrlInBrowser(PROJECT_REPOSITORY_URL)
                }
                HorizontalDivider(modifier = Modifier.padding(start = xxxxLarge))
                SettingListItem(
                    title = "project_readme",
                    subtitle = "project_readme_desc",
                    icon = IconData(MaterialSymbols.Rounded.School, themeExt.greenIconColor),
                ) {
                    uiSupport.openUrlInBrowser(PROJECT_README_URL)
                }
                HorizontalDivider(modifier = Modifier.padding(start = xxxxLarge))
                SettingListItem(
                    title = "change_log",
                    subtitle = "change_log_desc",
                    icon = IconData(MaterialSymbols.Rounded.Auto_awesome, themeExt.purpleIconColor),
                ) {
                    navigationManager.navigateAndClearStack(ChangeLog)
                }
            }
        }

        Spacer(modifier = Modifier.height(xxLarge))

        // Support Section
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(small),
        ) {
            Text(
                text = copywriter.getText("support"),
                style =
                    MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            SettingSectionCard {
                SettingListItem(
                    title = "feedback",
                    subtitle = "feedback_desc",
                    icon = IconData(MaterialSymbols.Rounded.Feedback, themeExt.amberIconColor),
                ) {
                    uiSupport.openUrlInBrowser(appUrls.issueTrackerUrl)
                }
                HorizontalDivider(modifier = Modifier.padding(start = xxxxLarge))
                SettingListItem(
                    title = "contact_us",
                    subtitle = "contact_us_desc",
                    icon = IconData(MaterialSymbols.Rounded.Mail, themeExt.cyanIconColor),
                ) {
                    uiSupport.openEmailClient("rob@ngduk.co.uk")
                }
            }
        }

        // Footer
        Column(
            modifier = Modifier.padding(vertical = medium),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(tiny),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "PasteFlow Dev MVP",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "-",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    modifier = Modifier.clickable { uiSupport.openUrlInBrowser(PROJECT_LICENSE_URL) },
                    text = "AGPL-3.0",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Text(
                text = "Local-first clipboard MVP based on CrossPaste",
                style =
                    MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 0.5.sp,
                    ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun SocialLinkButton(
    icon: ImageVector,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun SocialLinkButton(
    icon: Painter,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
