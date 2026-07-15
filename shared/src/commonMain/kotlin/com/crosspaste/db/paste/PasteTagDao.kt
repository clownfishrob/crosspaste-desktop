package com.crosspaste.db.paste

import com.crosspaste.paste.PasteTag
import kotlinx.coroutines.flow.Flow

interface PasteTagDao : QueryPasteTag {

    // Emits the primary (lowest sort order) tag colour for each of the given
    // paste ids that belongs to at least one tag, re-emitting whenever tag
    // membership changes. Ids without any tag are absent from the map.
    fun getPasteTagColorsFlow(pasteDataIds: List<Long>): Flow<Map<Long, Long>>

    suspend fun getMaxSortOrder(): Long

    suspend fun createPasteTag(
        name: String,
        color: Long,
    ): Long

    suspend fun updatePasteTagName(
        id: Long,
        name: String,
    )

    suspend fun updatePasteTagColor(
        id: Long,
        color: Long,
    )

    suspend fun updatePasteTagsSortOrder(orderedIds: List<Long>)

    suspend fun updatePasteTagSyncEnabled(
        id: Long,
        syncEnabled: Boolean,
    )

    suspend fun updatePasteTagSmartRule(
        id: Long,
        smartRule: String?,
    )

    fun switchPinPasteTagBlock(
        pasteDataId: Long,
        pasteTagId: Long,
    )

    suspend fun hasDisabledSyncTag(pasteDataId: Long): Boolean

    fun getPasteTagsBlock(pasteDataId: Long): List<Long>

    suspend fun addTagsToPastes(
        pasteDataIds: List<Long>,
        pasteTagIds: Set<Long>,
    )

    suspend fun countTagsForPastes(pasteDataIds: List<Long>): Map<Long, Int>

    fun deletePasteTagBlock(id: Long)

    fun getAllTagsBlock(): List<PasteTag>
}
