package com.carenest.data.source.remote.dto.paymob

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class PaymobIntentionSerializationTest {
    private val json = Json { encodeDefaults = true }

    @Test
    fun `intention request serializes using Paymob current field names`() {
        val encoded = json.encodeToString(
            PaymobIntentionRequestDto(
                amount = 50_000,
                currency = "EGP",
                paymentMethods = listOf(5855102),
                items = listOf(
                    PaymobIntentionItemDto(
                        name = "CareNest wallet top-up",
                        amount = 50_000,
                        description = "CareNest wallet credit",
                        quantity = 1,
                    ),
                ),
                billingData = PaymobBillingDataDto(
                    firstName = "CareNest",
                    lastName = "Patient",
                    email = "patient@carenest.local",
                    phoneNumber = "+201000000000",
                ),
                customer = PaymobCustomerDto(
                    firstName = "CareNest",
                    lastName = "Patient",
                    email = "patient@carenest.local",
                ),
                specialReference = "carenest-wallet-test",
            ),
        )

        val root = json.parseToJsonElement(encoded).jsonObject
        assertFalse(root.getValue("amount").jsonPrimitive.isString)
        assertEquals("50000", root.getValue("amount").jsonPrimitive.content)
        assertEquals("EGP", root.getValue("currency").jsonPrimitive.content)
        assertEquals("5855102", root.getValue("payment_methods").jsonArray.single().jsonPrimitive.content)
        assertEquals("carenest-wallet-test", root.getValue("special_reference").jsonPrimitive.content)
        assertEquals("CareNest", root.getValue("billing_data").jsonObject.getValue("first_name").jsonPrimitive.content)
        assertEquals("Patient", root.getValue("customer").jsonObject.getValue("last_name").jsonPrimitive.content)
    }
}
