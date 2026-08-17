package com.carenest.data.source.remote.dto.wallet

import java.math.BigDecimal
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonPrimitive

@Serializable
data class CreditResponseDto(
    @SerialName("credit") val credit: Double,
)

@Serializable
data class CreditUpdateRequestDto(
    @Serializable(with = BigDecimalJsonNumberSerializer::class)
    @SerialName("amount") val amount: BigDecimal,
    @SerialName("operation") val operation: String,
)

@Serializable
data class CreditUpdateResponseDto(
    @SerialName("credit") val credit: Double,
)

object BigDecimalJsonNumberSerializer : KSerializer<BigDecimal> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("BigDecimalJsonNumber", PrimitiveKind.DOUBLE)

    override fun serialize(encoder: Encoder, value: BigDecimal) {
        if (encoder is JsonEncoder) {
            encoder.encodeJsonElement(JsonPrimitive(value))
        } else {
            encoder.encodeString(value.toPlainString())
        }
    }

    override fun deserialize(decoder: Decoder): BigDecimal {
        if (decoder is JsonDecoder) {
            return decoder.decodeJsonElement().toString().toBigDecimal()
        }
        return decoder.decodeString().toBigDecimal()
    }
}
