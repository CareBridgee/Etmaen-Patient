package com.carenest.data.source.remote.dto.profile

import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.utils.io.ByteChannel
import io.ktor.utils.io.readRemaining
import kotlinx.coroutines.test.runTest
import kotlinx.io.readByteArray
import org.junit.Assert.assertTrue
import org.junit.Test

class PrimaryProfileIdentitySerializationTest {

    @Test
    fun `profile identity multipart includes account image url`() = runTest {
        val request = ProfileRequestDto(
            firstName = "Aalaa",
            lastName = "Adel",
            dateOfBirth = "2000-01-01",
            gender = "FEMALE",
            profileImageUrl = "https://example.com/aalaa.jpg",
        )

        val body = request.toMultipartFormData().bodyText()

        assertFormField(body, "firstName", "Aalaa")
        assertFormField(body, "lastName", "Adel")
        assertFormField(body, "profileImageUrl", "https://example.com/aalaa.jpg")
        assertFormField(body, "gender", "FEMALE")
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
