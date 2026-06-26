package com.crosspaste.paste.plugin.type

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DesktopTypePluginLimitsTest {

    @Test
    fun `text limit accepts small snippets and rejects text over two megabytes`() {
        assertTrue(DesktopTextTypePlugin.isWithinTextLimit("small snippet"))

        val oversized = "x".repeat((2 * 1024 * 1024) + 1)

        assertFalse(DesktopTextTypePlugin.isWithinTextLimit(oversized))
    }

    @Test
    fun `image limit accepts twenty megabytes and rejects larger images`() {
        assertTrue(DesktopImageTypePlugin.isWithinImageLimit(20L * 1024L * 1024L))
        assertFalse(DesktopImageTypePlugin.isWithinImageLimit((20L * 1024L * 1024L) + 1L))
    }
}
