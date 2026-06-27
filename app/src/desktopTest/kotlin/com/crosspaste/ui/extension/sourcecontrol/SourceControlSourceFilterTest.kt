package com.crosspaste.ui.extension.sourcecontrol

import com.crosspaste.paste.GuidePasteDataService
import kotlin.test.Test
import kotlin.test.assertEquals

class SourceControlSourceFilterTest {

    @Test
    fun `built-in guide sources are hidden from source control`() {
        val sources =
            listOf(
                "Safari",
                GuidePasteDataService.PASTEFLOW_DEV_GUIDE,
                "CrossPaste Guide",
                "Finder",
            )

        assertEquals(
            listOf("Safari", "Finder"),
            sources.withoutBuiltInGuideSources(),
        )
    }
}
