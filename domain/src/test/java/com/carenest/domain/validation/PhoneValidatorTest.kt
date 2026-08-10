package com.carenest.domain.validation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PhoneValidatorTest {

    @Test
    fun egypt_sanitizesValidatesFormatsAndBuildsInternationalNumber() {
        val country = SupportedPhoneCountry.EGYPT

        assertNull(PhoneValidator.validate("1027642749", country))
        assertEquals("1027642749", PhoneValidator.sanitize("01027642749", country))
        assertEquals("1027642749", PhoneValidator.sanitize("+201027642749", country))
        assertEquals("1027642749", PhoneValidator.sanitize("+20 102 764 2749", country))
        assertEquals("102 764 2749", PhoneValidator.format("1027642749", country))
        assertEquals("+201027642749", PhoneValidator.toInternationalNumber("1027642749", country))
    }

    @Test
    fun egypt_rejectsWrongLengthsAndPrefixes() {
        val country = SupportedPhoneCountry.EGYPT

        assertEquals(PhoneNumberValidationError.InvalidLength, PhoneValidator.validate("102764274", country))
        assertEquals(PhoneNumberValidationError.InvalidLength, PhoneValidator.validate("10276427499", country))
        assertEquals(PhoneNumberValidationError.InvalidFormat, PhoneValidator.validate("1327642749", country))
    }

    @Test
    fun saudi_sanitizesValidatesFormatsAndBuildsInternationalNumber() {
        val country = SupportedPhoneCountry.SAUDI_ARABIA

        assertNull(PhoneValidator.validate("501234567", country))
        assertEquals("501234567", PhoneValidator.sanitize("0501234567", country))
        assertEquals("501234567", PhoneValidator.sanitize("+966501234567", country))
        assertEquals("50 123 4567", PhoneValidator.format("501234567", country))
        assertEquals("+966501234567", PhoneValidator.toInternationalNumber("501234567", country))
    }

    @Test
    fun saudi_rejectsWrongLengthsAndNonMobileNumbers() {
        val country = SupportedPhoneCountry.SAUDI_ARABIA

        assertEquals(PhoneNumberValidationError.InvalidLength, PhoneValidator.validate("50123456", country))
        assertEquals(PhoneNumberValidationError.InvalidLength, PhoneValidator.validate("5012345678", country))
        assertEquals(PhoneNumberValidationError.InvalidFormat, PhoneValidator.validate("401234567", country))
    }

    @Test
    fun uae_acceptsSupportedPrefixesAndNormalizesInput() {
        val country = SupportedPhoneCountry.UAE

        listOf("50", "52", "54", "55", "56", "58").forEach { prefix ->
            assertNull(PhoneValidator.validate(prefix + "1234567", country))
        }
        assertEquals("501234567", PhoneValidator.sanitize("0501234567", country))
        assertEquals("501234567", PhoneValidator.sanitize("+971501234567", country))
        assertEquals("50 123 4567", PhoneValidator.format("501234567", country))
        assertEquals("+971501234567", PhoneValidator.toInternationalNumber("501234567", country))
    }

    @Test
    fun uae_rejectsWrongLengthsAndUnsupportedPrefixes() {
        val country = SupportedPhoneCountry.UAE

        assertEquals(PhoneNumberValidationError.InvalidLength, PhoneValidator.validate("50123456", country))
        assertEquals(PhoneNumberValidationError.InvalidLength, PhoneValidator.validate("5012345678", country))
        assertEquals(PhoneNumberValidationError.InvalidFormat, PhoneValidator.validate("511234567", country))
    }

    @Test
    fun fullSupportedNumbersAreNormalizedWithoutDuplicatingDialCodes() {
        assertEquals("+201027642749", PhoneValidator.normalizeInternationalNumber("+20 102 764 2749"))
        assertEquals("+966501234567", PhoneValidator.normalizeInternationalNumber("+966 50 123 4567"))
        assertEquals("+971501234567", PhoneValidator.normalizeInternationalNumber("+971 50 123 4567"))
    }

    @Test
    fun internationalNumbersAreFormattedForDisplay() {
        assertEquals("+20 102 764 2749", PhoneValidator.formatInternationalNumber("+201027642749"))
        assertEquals("+966 50 123 4567", PhoneValidator.formatInternationalNumber("+966501234567"))
        assertEquals("+971 50 123 4567", PhoneValidator.formatInternationalNumber("+971501234567"))
    }

    @Test
    fun unsupportedInternationalNumberIsLeftUnchanged() {
        assertEquals("+1 555 000 0000", PhoneValidator.formatInternationalNumber("+1 555 000 0000"))
    }

    @Test
    fun supportedCountryIsDetectedFromInternationalOrLegacyEgyptianNumber() {
        assertEquals(
            SupportedPhoneCountry.EGYPT,
            PhoneValidator.detectCountry("01027642749")
        )
        assertEquals(
            SupportedPhoneCountry.SAUDI_ARABIA,
            PhoneValidator.detectCountry("+966 50 123 4567")
        )
        assertEquals(
            SupportedPhoneCountry.UAE,
            PhoneValidator.detectCountry("+971 50 123 4567")
        )
        assertNull(PhoneValidator.detectCountry("+1 555 000 0000"))
    }
}
