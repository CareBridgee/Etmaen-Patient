package com.carenest.domain.validation

object PhoneValidator {
    private val phoneRegex = Regex("^\\+?[0-9\\s\\-()]{7,20}$")

    fun isValid(phone: String): Boolean {
        val trimmed = phone.trim()
        if (trimmed.isBlank()) return false
        val digitsOnly = trimmed.replace(Regex("[^0-9+]"), "")
        if (digitsOnly.length < 7 || digitsOnly.length > 15) return false
        return phoneRegex.matches(trimmed)
    }

    fun clean(phone: String): String {
        return phone.trim()
    }
}
