package com.carenest.data.socket.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.nullable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive

/**
 * Decodes java-time fields that Spring Boot may render either as ISO-8601 strings
 * or as timestamp arrays ([2026,8,25,6,57,57]) into plain strings.
 */
object FlexibleDateStringSerializer : KSerializer<String?> {
    private val delegate = String.serializer()

    override val descriptor: SerialDescriptor = delegate.descriptor.nullable

    override fun deserialize(decoder: Decoder): String? {
        val element = (decoder as JsonDecoder).decodeJsonElement()
        return when (element) {
            is JsonNull -> null
            is JsonPrimitive -> element.content
            is JsonArray -> formatTimestampArray(element)
            else -> null
        }
    }

    override fun serialize(encoder: Encoder, value: String?) {
        val jsonEncoder = encoder as JsonEncoder
        if (value == null) jsonEncoder.encodeJsonElement(JsonNull)
        else jsonEncoder.encodeJsonElement(JsonPrimitive(value))
    }

    private fun formatTimestampArray(array: JsonArray): String? {
        val parts = array.map { (it as? JsonPrimitive)?.content ?: return null }
        val looksLikeDate = parts.firstOrNull()?.toIntOrNull()?.let { it >= 1000 } == true
        return when {
            looksLikeDate && parts.size >= 4 -> buildString {
                append(parts[0]).append('-').append(parts[1]).append('-').append(parts[2])
                append('T')
                append(parts[3]).append(':').append(parts.getOrElse(4) { "0" })
                append(':').append(parts.getOrElse(5) { "0" })
            }
            looksLikeDate -> parts.joinToString("-")
            else -> parts.joinToString(":")
        }
    }
}
