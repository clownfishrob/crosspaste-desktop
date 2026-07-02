package com.crosspaste.paste

interface PasteExportParamFactory<T> {

    fun createPasteExportParam(
        types: Set<Long>,
        onlyTagged: Boolean,
        tagId: Long?,
        maxFileSize: Long?,
        exportPath: T,
    ): PasteExportParam
}
