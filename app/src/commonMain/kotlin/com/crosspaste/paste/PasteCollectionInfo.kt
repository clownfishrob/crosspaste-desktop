package com.crosspaste.paste

import kotlinx.serialization.Serializable

/**
 * Collection (tag) metadata bundled with a single-collection export so that
 * import can recreate the collection and re-pin the imported items to it.
 */
@Serializable
data class PasteCollectionInfo(
    val name: String,
    val color: Long,
) {
    companion object {
        const val COLLECTION_INFO_FILE = "collection.info"
    }
}
