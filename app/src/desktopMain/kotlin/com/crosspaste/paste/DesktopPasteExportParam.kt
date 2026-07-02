package com.crosspaste.paste

import com.crosspaste.utils.getFileUtils
import okio.BufferedSink
import okio.Path
import okio.buffer

class DesktopPasteExportParam(
    types: Set<Long>,
    onlyTagged: Boolean,
    tagId: Long?,
    maxFileSize: Long?,
    private val exportPath: Path,
) : PasteExportParam(types, onlyTagged, tagId, maxFileSize) {

    private val fileUtils = getFileUtils()

    override fun exportBufferedSink(fileName: String): BufferedSink? {
        val targetZipFile = exportPath.resolve(fileName)
        return fileUtils.fileSystem.sink(targetZipFile).buffer()
    }
}
