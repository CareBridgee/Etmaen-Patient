package com.carenest.presentation.ui.auth.register

data class RegisterState(
    val firstName: String = "",
    val lastName: String = "",
    val dateOfBirth: String = "",
    val gender: String = "",
    val profileId: String? = null,
    val isInitializing: Boolean = true,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null
)

sealed interface RegisterIntent {
    data class FirstNameChanged(val firstName: String) : RegisterIntent
    data class LastNameChanged(val lastName: String) : RegisterIntent
    data class DateOfBirthChanged(val dateOfBirth: String) : RegisterIntent
    data class GenderChanged(val gender: String) : RegisterIntent
    data object BackClicked : RegisterIntent
    data object ContinueClicked : RegisterIntent
}

sealed interface RegisterEffect {
    data object NavigateBack : RegisterEffect
    data object NavigateToWelcome : RegisterEffect
}
