package com.crosspaste.db.paste

import com.crosspaste.paste.PasteTag
import kotlinx.coroutines.flow.Flow

interface PasteTagDao : QueryPasteTag {

    // Emits the subset of the given paste ids that belong to at least one tag,
    // re-emitting whenever tag membership changes.
    fun getTaggedPasteIdsFlow(pasteDataIds: List<Long>): Flow<Set<Long>>

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

    fun switchPinPasteTagBlock(
        pasteDataId: Long,
        pasteTagId: Long,
    )

    fun getPasteTagsBlock(pasteDataId: Long): List<Long>

    suspend fun addTagsToPastes(
        pasteDataIds: List<Long>,
        pasteTagIds: Set<Long>,
    )

    suspend fun countTagsForPastes(pasteDataIds: List<Long>): Map<Long, Int>

    fun deletePasteTagBlock(id: Long)

    fun getAllTagsBlock(): List<PasteTag>
}
