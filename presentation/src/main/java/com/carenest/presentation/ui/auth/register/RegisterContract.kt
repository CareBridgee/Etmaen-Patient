package com.carenest.presentation.ui.auth.register

data class RegisterState(
    val firstName: String = "",
    val lastName: String = "",
    val dateOfBirth: String = "",
    val nationalId: String = "",
    val gender: String = "",
    val accountType: String = ""
)

sealed interface RegisterIntent {
    data class FirstNameChanged(val firstName: String) : RegisterIntent
    data class LastNameChanged(val lastName: String) : RegisterIntent
    data class DateOfBirthChanged(val dateOfBirth: String) : RegisterIntent
    data class NationalIdChanged(val nationalId: String) : RegisterIntent
    data class GenderChanged(val gender: String) : RegisterIntent
    data class AccountTypeChanged(val accountType: String) : RegisterIntent
    data object BackClicked : RegisterIntent
    data object ContinueClicked : RegisterIntent
}

sealed interface RegisterEffect {
    data object NavigateBack : RegisterEffect
    data object NavigateToWelcome : RegisterEffect
}
