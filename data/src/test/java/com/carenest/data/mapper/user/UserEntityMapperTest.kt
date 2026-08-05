package com.carenest.data.mapper.user

import com.carenest.domain.model.home.User
import org.junit.Assert.assertEquals
import org.junit.Test

class UserEntityMapperTest {
    @Test
    fun `domain entity round trip keeps all persisted fields`() {
        val expected = User(
            id = "user-id",
            phoneNumber = "+201000000000",
            email = "user@example.com",
            firstName = "Aalaa",
            lastName = "Adel",
            dateOfBirth = "1990-01-01",
            gender = "FEMALE",
            profileImageUrl = "https://example.com/avatar.png",
            isDeleted = false,
            createdAt = "2026-08-05T07:09:57",
            updatedAt = "2026-08-05T07:14:53",
            lastLoginAt = "2026-08-05T07:09:57",
            defaultProfileId = "profile-id"
        )

        assertEquals(expected, expected.toEntity().toDomain())
    }
}
