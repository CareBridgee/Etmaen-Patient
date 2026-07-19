package com.carenest.presentation.ui.auth.registration

data class RegistrationState(
    val firstName: String = "",
    val lastName: String = "",
    val dateOfBirth: String = "",
    val nationalId: String = "",
    val gender: String = "",
    val accountType: String = ""
)

sealed interface RegistrationIntent {
    data class FirstNameChanged(val firstName: String) : RegistrationIntent
    data class LastNameChanged(val lastName: String) : RegistrationIntent
    data class DateOfBirthChanged(val dateOfBirth: String) : RegistrationIntent
    data class NationalIdChanged(val nationalId: String) : RegistrationIntent
    data class GenderChanged(val gender: String) : RegistrationIntent
    data class AccountTypeChanged(val accountType: String) : RegistrationIntent
    data object BackClicked : RegistrationIntent
    data object ContinueClicked : RegistrationIntent
}

sealed interface RegistrationEffect {
    data object NavigateBack : RegistrationEffect
    data object NavigateToWelcome : RegistrationEffect
}
