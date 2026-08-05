package com.carenest.data.mapper.user

import com.carenest.data.source.remote.dto.user.UserResponseDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class UserDtoMapperTest {
    @Test
    fun `maps optional and default profile fields`() {
        val user = UserResponseDto(
            id = "user-id",
            phoneNumber = "+201000000000",
            email = null,
            firstName = "Aalaa",
            defaultProfileId = "profile-id"
        ).toDomain()

        assertEquals("user-id", user.id)
        assertEquals("+201000000000", user.phoneNumber)
        assertEquals("Aalaa", user.firstName)
        assertEquals("profile-id", user.defaultProfileId)
        assertNull(user.email)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `rejects response without an id`() {
        UserResponseDto(phoneNumber = "+201000000000").toDomain()
    }
}
