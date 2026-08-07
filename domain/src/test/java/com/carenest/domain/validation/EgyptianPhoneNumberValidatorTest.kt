package com.carenest.domain.validation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class EgyptianPhoneNumberValidatorTest {

    @Test
    fun `blank phone is required`() {
        assertEquals(
            PhoneNumberValidationError.Required,
            EgyptianPhoneNumberValidator.validate("")
        )
    }

    @Test
    fun `phone must contain exactly eleven digits`() {
        assertEquals(
            PhoneNumberValidationError.InvalidLength,
            EgyptianPhoneNumberValidator.validate("0101234567")
        )
        assertEquals(
            PhoneNumberValidationError.InvalidLength,
            EgyptianPhoneNumberValidator.validate("010123456789")
        )
    }

    @Test
    fun `phone must use an Egyptian mobile prefix`() {
        assertEquals(
            PhoneNumberValidationError.InvalidFormat,
            EgyptianPhoneNumberValidator.validate("01312345678")
        )
    }

    @Test
    fun `supported Egyptian mobile prefixes are valid`() {
        listOf("01012345678", "01112345678", "01212345678", "01512345678").forEach {
            assertNull(EgyptianPhoneNumberValidator.validate(it))
        }
    }

    @Test
    fun `input sanitization keeps at most eleven digits`() {
        assertEquals(
            "01012345678",
            EgyptianPhoneNumberValidator.sanitizeInput("010-1234-5678-extra")
        )
    }
}
