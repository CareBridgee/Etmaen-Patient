package com.carenest.data.source.remote.dto.profile

import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.utils.io.ByteChannel
import io.ktor.utils.io.readRemaining
import kotlinx.coroutines.test.runTest
import kotlinx.io.readByteArray
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProfileRequestSerializationTest {
    private val json = Json {
        encodeDefaults = true
        explicitNulls = false
    }

    @Test
    fun `medical history multipart omits height and weight`() = runTest {
        val body = ProfileRequestDto(
            previousSurgeries = "Appendectomy",
            previousHospitalizations = "None"
        ).toMultipartFormData().bodyText()

        assertFormField(body, "previousSurgeries", "Appendectomy")
        assertFormField(body, "previousHospitalizations", "None")
        assertFalse(body.contains("name=height"))
        assertFalse(body.contains("name=weight"))
    }

    @Test
    fun `mobility multipart contains notes and omits height and weight`() = runTest {
        val body = ProfileRequestDto(
            mobilityStatus = "INDEPENDENT",
            mobilityNotes = "Needs help when using stairs"
        ).toMultipartFormData().bodyText()

        assertFormField(body, "mobilityStatus", "INDEPENDENT")
        assertFormField(body, "mobilityNotes", "Needs help when using stairs")
        assertFalse(body.contains("name=height"))
        assertFalse(body.contains("name=weight"))
    }

    @Test
    fun `basic health multipart still contains height and weight`() = runTest {
        val body = ProfileRequestDto(
            bloodType = "A+",
            height = 160.0,
            weight = 66.0
        ).toMultipartFormData().bodyText()

        assertFormField(body, "bloodType", "A+")
        assertFormField(body, "height", "160.0")
        assertFormField(body, "weight", "66.0")
    }

    @Test
    fun `allergy request serializes to name only`() {
        assertEquals(
            "{\"name\":\"Aspirin\"}",
            json.encodeToString(ProfileAllergyRequestDto(name = "Aspirin"))
        )
    }

    @Test
    fun `medication request serializes to name only`() {
        assertEquals(
            "{\"name\":\"test\"}",
            json.encodeToString(ProfileMedicationRequestDto(name = "test"))
        )
    }

    @Test
    fun `medical condition request remains name only`() {
        assertEquals(
            "{\"name\":\"Diabetes\"}",
            json.encodeToString(ProfileMedicalConditionRequestDto(name = "Diabetes"))
        )
    }

    private suspend fun MultiPartFormDataContent.bodyText(): String {
        val channel = ByteChannel()
        writeTo(channel)
        channel.flushAndClose()
        return channel.readRemaining().readByteArray().decodeToString()
    }

    private fun assertFormField(body: String, name: String, value: String) {
        assertTrue(body.contains("name=$name"))
        assertTrue(body.contains("\r\n\r\n$value\r\n"))
    }
}
