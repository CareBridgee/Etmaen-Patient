package com.carenest.domain.validation

import com.carenest.domain.model.profile.EmergencyRelationship
import org.junit.Assert.assertEquals
import org.junit.Test

class ProfileValidatorEmergencyContactTest {

    @Test
    fun `emergency phone is validated and saved in international format`() {
        val egyptian = ProfileValidator.emergencyContact(
            name = "Ahmed Ali",
            relationship = EmergencyRelationship.Father,
            phoneNumber = "1027642749",
            phoneCountry = SupportedPhoneCountry.EGYPT
        )
        val saudi = ProfileValidator.emergencyContact(
            name = "Ahmed Ali",
            relationship = EmergencyRelationship.Friend,
            phoneNumber = "501234567",
            phoneCountry = SupportedPhoneCountry.SAUDI_ARABIA
        )
        val emirates = ProfileValidator.emergencyContact(
            name = "Ahmed Ali",
            relationship = EmergencyRelationship.Relative,
            phoneNumber = "501234567",
            phoneCountry = SupportedPhoneCountry.UAE
        )

        assertEquals("+201027642749", egyptian.phoneNumber)
        assertEquals("+966501234567", saudi.phoneNumber)
        assertEquals("+971501234567", emirates.phoneNumber)
    }
}
