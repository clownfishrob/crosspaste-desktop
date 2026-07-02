package com.crosspaste.paste

import okio.Path

class DesktopPasteExportParamFactory : PasteExportParamFactory<Path> {
    override fun createPasteExportParam(
        types: Set<Long>,
        onlyTagged: Boolean,
        tagId: Long?,
        maxFileSize: Long?,
        exportPath: Path,
    ): PasteExportParam = DesktopPasteExportParam(types, onlyTagged, tagId, maxFileSize, exportPath)
}
