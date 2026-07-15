package com.crosspaste.paste

object SnippetTemplate {

    private val tokenRegex = Regex("""\{([A-Za-z][A-Za-z0-9_]*)}""")

    fun expand(
        template: String,
        variables: Map<String, String>,
    ): String =
        tokenRegex.replace(template) { match ->
            variables[match.groupValues[1]] ?: match.value
        }
}
