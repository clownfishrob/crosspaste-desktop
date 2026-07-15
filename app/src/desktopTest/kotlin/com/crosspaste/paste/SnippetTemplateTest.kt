package com.crosspaste.paste

import kotlin.test.Test
import kotlin.test.assertEquals

class SnippetTemplateTest {

    @Test
    fun `expands known variables and leaves unknown variables unchanged`() {
        val result =
            SnippetTemplate.expand(
                template = "Hello {name}, today is {date}. Keep {unknown}.",
                variables =
                    mapOf(
                        "name" to "Robert",
                        "date" to "2026-07-15",
                    ),
            )

        assertEquals("Hello Robert, today is 2026-07-15. Keep {unknown}.", result)
    }
}
