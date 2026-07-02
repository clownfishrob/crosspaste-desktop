package com.crosspaste.paste

import okio.BufferedSink

abstract class PasteExportParam(
    val types: Set<Long>,
    val onlyTagged: Boolean,
    // When set, export only items pinned to this tag; takes precedence over onlyTagged.
    val tagId: Long?,
    val maxFileSize: Long?,
) {

    abstract fun exportBufferedSink(fileName: String): BufferedSink?
}
