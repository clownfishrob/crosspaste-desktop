package com.crosspaste.paste

import com.crosspaste.paste.PasteType.Companion.IMAGE_TYPE
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SmartCollectionRuleTest {

    @Test
    fun `empty rule has no filters`() {
        assertTrue(SmartCollectionRule().isEmpty())
    }

    @Test
    fun `rule with filters is not empty and serializes cleanly`() {
        val rule =
            SmartCollectionRule(
                searchQuery = "invoice",
                pasteTypes = listOf(IMAGE_TYPE.type),
                source = "Skitch",
                includeRemote = false,
            )

        val encoded = Json.encodeToString(rule)
        val decoded = Json.decodeFromString<SmartCollectionRule>(encoded)

        assertFalse(rule.isEmpty())
        assertEquals(rule, decoded)
    }
}
