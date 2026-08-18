package com.carenest.data.source.remote.dto.wallet

import java.math.BigDecimal
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class CreditUpdateSerializationTest {
    private val json = Json { encodeDefaults = true }

    @Test
    fun `credit update amount serializes as JSON number token`() {
        val encoded = json.encodeToString(
            CreditUpdateRequestDto(
                amount = BigDecimal("500.00"),
                operation = "ADD",
            ),
        )

        val amountToken = json.parseToJsonElement(encoded).jsonObject.getValue("amount").jsonPrimitive
        assertFalse(amountToken.isString)
        assertEquals(0, amountToken.content.toBigDecimal().compareTo(BigDecimal("500.00")))
        assertEquals("ADD", json.parseToJsonElement(encoded).jsonObject.getValue("operation").jsonPrimitive.content)
    }
}
