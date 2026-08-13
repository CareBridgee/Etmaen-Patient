package com.carenest.domain.validation

import com.carenest.domain.model.profile.ProfileField
import com.carenest.domain.model.profile.ProfileValidationError
import com.carenest.domain.model.profile.ProfileValidationException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

class ProfileValidatorPersonalInfoAndHealthTest {

    @Test
    fun `current local date is accepted as date of birth`() {
        val now = Date()
        val today = SimpleDateFormat("MM/dd/yyyy", Locale.US).format(now)

        val result = ProfileValidator.personalInfo("Aya", "Adel", today, "FEMALE")

        assertEquals(
            SimpleDateFormat("yyyy-MM-dd", Locale.US).format(now),
            result.dateOfBirth
        )
    }

    @Test
    fun `tomorrow is rejected as date of birth`() {
        val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 1) }.time
        val value = SimpleDateFormat("MM/dd/yyyy", Locale.US).format(tomorrow)

        val error = validationError {
            ProfileValidator.personalInfo("Aya", "Adel", value, "FEMALE")
        }

        assertEquals(ProfileValidationError.FutureDate, error.fieldErrors[ProfileField.DateOfBirth])
    }

    @Test
    fun `one kilogram is rejected while lower boundary is accepted`() {
        val error = validationError {
            ProfileValidator.basicHealth(height = "170", weight = "1", bloodType = "A+")
        }

        assertEquals(ProfileValidationError.WeightOutOfRange, error.fieldErrors[ProfileField.Weight])
        assertEquals(2.0, ProfileValidator.basicHealth("170", "2", "A+").weight, 0.0)
    }

    private fun validationError(block: () -> Unit): ProfileValidationException {
        try {
            block()
            fail("Expected ProfileValidationException")
        } catch (error: ProfileValidationException) {
            return error
        }
        throw AssertionError("Unreachable")
    }
}
