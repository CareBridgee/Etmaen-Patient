package com.carenest.data.source.remote.dto

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertTrue
import org.junit.Test

class ServiceRequestPaymentSerializationTest {
    private val json = Json {
        encodeDefaults = true
        explicitNulls = false
    }

    @Test
    fun `create service request serializes selected payment type`() {
        val body = CreateServiceRequestDto(
            profileId = "11111111-1111-1111-1111-111111111111",
            serviceTypeId = "22222222-2222-2222-2222-222222222222",
            latitude = 30.0444,
            longitude = 31.2357,
            address = "Cairo, Egypt",
            district = "Cairo",
            apartment = "4B",
            preferredDate = "2026-08-17",
            preferredTime = "09:00:00",
            serviceDescription = "Needs nursing care",
            paymentType = "CREDIT",
        )

        val encoded = json.encodeToString(body)

        assertTrue(encoded.contains("\"paymentType\":\"CREDIT\""))
    }
}
