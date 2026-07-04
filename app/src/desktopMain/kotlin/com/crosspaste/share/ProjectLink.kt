package com.crosspaste.share

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.composables.icons.materialsymbols.MaterialSymbols
import com.composables.icons.materialsymbols.rounded.Language
import com.crosspaste.ui.base.UISupport
import com.crosspaste.ui.theme.AppUISize.huge
import com.crosspaste.ui.theme.AppUISize.mediumRoundedCornerShape
import com.crosspaste.ui.theme.AppUISize.xxLarge

class ProjectLink(
    private val uiSupport: UISupport,
) : AppSharePlatform {
    override val platformName: String = "Project"

    @Composable
    override fun ButtonPlatform() {
        Box(
            modifier =
                Modifier
                    .size(huge)
                    .background(
                        Color(0xFFE8F5E9),
                        mediumRoundedCornerShape,
                    ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = MaterialSymbols.Rounded.Language,
                contentDescription = "project",
                modifier = Modifier.size(xxLarge),
                tint = Color(0xFF2E7D32),
            )
        }
    }

    override suspend fun action(appShareService: AppShareService) {
        uiSupport.openUrlInBrowser(appShareService.getShareUrl())
    }
}
