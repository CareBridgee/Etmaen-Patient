package com.carenest.presentation.ui.auth.register

sealed interface RegisterIntent {
    data class FirstNameChanged(val firstName: String) : RegisterIntent
    data class LastNameChanged(val lastName: String) : RegisterIntent
    data class DobChanged(val dob: String) : RegisterIntent
    data class GenderChanged(val gender: String) : RegisterIntent

    data object CompleteProfileClicked : RegisterIntent
    data object SkipClicked : RegisterIntent
    data object ContinueClicked : RegisterIntent
    data object BackClicked : RegisterIntent
}

enum class RegisterStep {
    WELCOME,
    PERSONAL_INFO
}

data class RegisterState(
    val currentStep: RegisterStep = RegisterStep.WELCOME,
    val firstName: String = "",
    val lastName: String = "",
    val dateOfBirth: String = "",
    val gender: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)
