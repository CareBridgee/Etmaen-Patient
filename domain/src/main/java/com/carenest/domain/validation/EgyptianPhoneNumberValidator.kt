package com.carenest.domain.validation

enum class PhoneNumberValidationError {
    Required,
    InvalidLength,
    InvalidFormat
}

object EgyptianPhoneNumberValidator {
    const val LENGTH = 11

    private val mobileNumberRegex = Regex("^01[0125][0-9]{8}$")

    fun validate(phoneNumber: String): PhoneNumberValidationError? = when {
        phoneNumber.isBlank() -> PhoneNumberValidationError.Required
        phoneNumber.length != LENGTH -> PhoneNumberValidationError.InvalidLength
        !mobileNumberRegex.matches(phoneNumber) -> PhoneNumberValidationError.InvalidFormat
        else -> null
    }

    fun validateOptional(phoneNumber: String): PhoneNumberValidationError? {
        if (phoneNumber.isBlank()) return null
        return validate(phoneNumber)
    }

    fun sanitizeInput(input: String): String {
        var digits = input.filter { it in '0'..'9' }
        if (digits.startsWith("20") && digits.length > 11) {
            digits = digits.drop(2)
        }
        return digits.take(LENGTH)
    }
}
