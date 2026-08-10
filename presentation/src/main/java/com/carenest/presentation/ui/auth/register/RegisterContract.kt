package com.carenest.presentation.ui.auth.register

import com.carenest.domain.model.profile.ProfileField
import com.carenest.domain.model.profile.ProfileValidationError
import com.carenest.presentation.ui.auth.AuthUiError

data class RegisterState(
    val mode: PersonalInformationMode = PersonalInformationMode.Registration,
    val firstName: String = "",
    val lastName: String = "",
    val dateOfBirth: String = "",
    val gender: String = "MALE",
    val avatarUri: String? = null,
    val profileImageUrl: String? = null,
    val selectedAvatarBytes: ByteArray? = null,
    val selectedAvatarFileName: String? = null,
    val selectedAvatarContentType: String? = null,
    val isInitializing: Boolean = true,
    val isSubmitting: Boolean = false,
    val errorMessage: AuthUiError? = null,
    val validationErrors: Map<ProfileField, ProfileValidationError> = emptyMap()
)

sealed interface RegisterIntent {
    data class ConfigureMode(val mode: PersonalInformationMode) : RegisterIntent
    data class FirstNameChanged(val firstName: String) : RegisterIntent
    data class LastNameChanged(val lastName: String) : RegisterIntent
    data class DateOfBirthChanged(val dateOfBirth: String) : RegisterIntent
    data class GenderChanged(val gender: String) : RegisterIntent
    data class AvatarSelected(
        val uri: String,
        val fileName: String,
        val contentType: String,
        val bytes: ByteArray
    ) : RegisterIntent
    data object EditAvatarClicked : RegisterIntent
    data object BackClicked : RegisterIntent
    data object ContinueClicked : RegisterIntent
}

sealed interface RegisterEffect {
    data object NavigateBack : RegisterEffect
    data class NavigateToWelcome(val profileId: String? = null) : RegisterEffect
    data object NavigateToHome : RegisterEffect
    data object NavigateAfterEdit : RegisterEffect
    data object SelectAvatar : RegisterEffect
}

enum class PersonalInformationMode {
    Registration,
    EditProfile
}
