package com.crosspaste.share

import com.crosspaste.i18n.GlobalCopywriter
import com.crosspaste.notification.NotificationManager
import com.crosspaste.paste.PasteboardService
import com.crosspaste.ui.base.UISupport

class DesktopAppShareService(
    private val copywriter: GlobalCopywriter,
    notificationManager: NotificationManager,
    pasteboardService: PasteboardService,
    private val uiSupport: UISupport,
) : AppShareService {
    override val appSharePlatformList: List<AppSharePlatform> =
        listOf(
            Clipboard(notificationManager, pasteboardService),
            Mail(notificationManager, pasteboardService, uiSupport),
            ProjectLink(uiSupport),
        )
    override val shareContentKey: String = "share_content"

    override val shareTitleKey: String = "share_title"

    override fun getShareText(): String =
        buildString {
            appendLine(copywriter.getText(shareTitleKey))
            appendLine(copywriter.getText(shareContentKey))
            append(getShareUrl())
        }

    override fun getShareTitle(): String = copywriter.getText(shareTitleKey)

    override fun getShareContent(): String = copywriter.getText(shareContentKey)

    override fun getShareUrl(): String = uiSupport.getCrossPasteWebUrl()
}
