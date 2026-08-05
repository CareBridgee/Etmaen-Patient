package com.carenest.domain.model.user

import com.carenest.domain.model.home.User
import com.carenest.domain.model.profile.Profile
import org.junit.Assert.assertEquals
import org.junit.Test

class AuthenticatedDestinationResolverTest {
    @Test
    fun `incomplete user routes to registration`() {
        assertEquals(
            AuthenticatedDestination.Registration,
            AuthenticatedDestinationResolver.resolve(User(id = "user-id"))
        )
    }

    @Test
    fun `complete user with incomplete default profile routes to completion`() {
        assertEquals(
            AuthenticatedDestination.CompleteProfile,
            AuthenticatedDestinationResolver.resolve(completeUser(), profile(height = null), true)
        )
    }

    @Test
    fun `complete user and profile route home`() {
        assertEquals(
            AuthenticatedDestination.Home,
            AuthenticatedDestinationResolver.resolve(completeUser(), profile(), true)
        )
    }

    private fun completeUser() = User(
        id = "user-id",
        firstName = "Aalaa",
        lastName = "Adel",
        dateOfBirth = "1990-01-01",
        gender = "FEMALE",
        defaultProfileId = "profile-id"
    )

    private fun profile(height: Double? = 170.0) = Profile(
        id = "profile-id",
        userId = "user-id",
        relationship = "SELF",
        firstName = null,
        lastName = null,
        dateOfBirth = null,
        gender = null,
        bloodType = "A+",
        height = height,
        weight = 70.0,
        mobilityStatus = "Independent",
        mobilityNotes = null,
        previousSurgeries = null,
        previousHospitalizations = null
    )
}
