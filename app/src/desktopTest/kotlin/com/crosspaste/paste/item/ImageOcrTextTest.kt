package com.crosspaste.paste.item

import com.crosspaste.presist.SingleFileInfoTree
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ImageOcrTextTest {

    private fun imageItem(): ImagesPasteItem =
        CreatePasteItemHelper.createImagesPasteItem(
            identifiers = listOf("image/png"),
            relativePathList = listOf("images/1/screenshot.png"),
            fileInfoTreeMap = mapOf("screenshot.png" to SingleFileInfoTree(size = 100, hash = "image-hash")),
        )

    @Test
    fun `image item stores OCR text in extra info`() {
        val item = imageItem().withOcrText("Invoice total 42 pounds")

        assertEquals("Invoice total 42 pounds", item.getOcrText())
        val storedText =
            item.extraInfo
                ?.get(PasteItemProperties.OCR_TEXT)
                ?.jsonPrimitive
                ?.content
        assertEquals("Invoice total 42 pounds", storedText)
    }

    @Test
    fun `image OCR text is included in search content`() {
        val item = imageItem().withOcrText("Skitch export invoice total")
        val searchContent = DefaultPasteItemReader().getSearchContent(item)

        assertNotNull(searchContent)
        assertTrue(searchContent.contains("screenshot.png"))
        assertTrue(searchContent.contains("skitch export invoice total"))
    }
}
