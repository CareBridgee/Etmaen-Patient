package com.carenest.presentation.ui.auth.register

import com.carenest.domain.model.profile.ProfileField
import com.carenest.domain.model.profile.ProfileValidationError

data class RegisterState(
    val mode: PersonalInformationMode = PersonalInformationMode.Registration,
    val firstName: String = "",
    val lastName: String = "",
    val dateOfBirth: String = "",
    val gender: String = "",
    val isInitializing: Boolean = true,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val validationErrors: Map<ProfileField, ProfileValidationError> = emptyMap()
)

sealed interface RegisterIntent {
    data class ConfigureMode(val mode: PersonalInformationMode) : RegisterIntent
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
    data object NavigateToHome : RegisterEffect
    data object NavigateAfterEdit : RegisterEffect
}

enum class PersonalInformationMode {
    Registration,
    EditProfile
}
