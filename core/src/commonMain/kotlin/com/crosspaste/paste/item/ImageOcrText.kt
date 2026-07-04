package com.crosspaste.paste.item

import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

fun ImagesPasteItem.getOcrText(): String? =
    extraInfo
        ?.get(PasteItemProperties.OCR_TEXT)
        ?.jsonPrimitive
        ?.content
        ?.takeIf { it.isNotBlank() }

fun ImagesPasteItem.withOcrText(text: String): ImagesPasteItem =
    copy(
        extraInfo =
            buildJsonObject {
                extraInfo?.forEach { (key, value) ->
                    put(key, value)
                }
                put(PasteItemProperties.OCR_TEXT, text.trim())
            },
    )
