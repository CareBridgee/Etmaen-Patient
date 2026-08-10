package com.carenest.presentation.ui.auth.register

internal fun formatDateInput(previousValue: String, newValue: String): String {
    val sanitizedValue = newValue
        .filter { it.isDigit() || it == '/' }
        .take(10)

    val userDeletedSeparator = sanitizedValue.length < previousValue.length &&
        sanitizedValue.count { it == '/' } < previousValue.count { it == '/' }
    if (userDeletedSeparator) return sanitizedValue

    val digits = sanitizedValue.filter(Char::isDigit).take(8)
    return buildString {
        digits.forEachIndexed { index, digit ->
            if (index == 2 || index == 4) append('/')
            append(digit)
        }
        if (digits.length == 2 || digits.length == 4) append('/')
    }
}
