package com.crosspaste.paste

import kotlinx.serialization.Serializable

@Serializable
data class SmartCollectionRule(
    val searchQuery: String = "",
    val pasteTypes: List<Int> = emptyList(),
    val source: String? = null,
    val includeRemote: Boolean = true,
) {
    fun isEmpty(): Boolean =
        searchQuery.isBlank() &&
            pasteTypes.isEmpty() &&
            source.isNullOrBlank() &&
            includeRemote
}
