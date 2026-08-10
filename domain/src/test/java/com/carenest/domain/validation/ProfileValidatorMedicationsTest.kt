package com.carenest.domain.validation

import com.carenest.domain.model.profile.MedicationInput
import com.carenest.domain.model.profile.ProfileField
import com.carenest.domain.model.profile.ProfileValidationError
import com.carenest.domain.model.profile.ProfileValidationException
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

class ProfileValidatorMedicationsTest {

    @Test
    fun `blank draft after a completed medication does not block continue`() {
        val result = ProfileValidator.medications(
            hasNoCurrentMedications = false,
            entries = listOf(
                MedicationInput(uiKey = 1L, name = "  Aspirin  "),
                MedicationInput(uiKey = 2L, name = "")
            )
        )

        assertEquals(listOf("Aspirin"), result.map { it.name })
    }

    @Test
    fun `an entirely blank medication list still shows a useful field error`() {
        val failure = validationFailure {
            ProfileValidator.medications(
                hasNoCurrentMedications = false,
                entries = listOf(MedicationInput(uiKey = 7L, name = "   "))
            )
        }

        assertEquals(
            ProfileValidationError.MedicationNameRequired,
            failure.medicationErrors[7L]?.name
        )
    }

    @Test
    fun `missing medication rows still requires a selection`() {
        val failure = validationFailure {
            ProfileValidator.medications(
                hasNoCurrentMedications = false,
                entries = emptyList()
            )
        }

        assertEquals(
            ProfileValidationError.MedicationSelectionRequired,
            failure.fieldErrors[ProfileField.MedicationsSelection]
        )
    }

    private fun validationFailure(block: () -> Unit): ProfileValidationException = try {
        block()
        fail("Expected medication validation to fail")
        error("Unreachable")
    } catch (error: ProfileValidationException) {
        error
    }
}
