package com.carenest.presentation.ui.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.domain.model.profile.ProfileField
import com.carenest.domain.model.profile.ProfileValidationException
import com.carenest.domain.model.user.AuthenticatedDestination
import com.carenest.domain.usecase.user.GetAuthenticatedDestinationUseCase
import com.carenest.domain.usecase.user.ObserveCurrentUserUseCase
import com.carenest.domain.usecase.user.UpdateCurrentUserUseCase
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import com.carenest.domain.repository.UserRepository
import kotlinx.coroutines.launch
import com.carenest.presentation.ui.auth.AuthUiError
import com.carenest.presentation.ui.auth.toAuthUiError

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val observeCurrentUser: ObserveCurrentUserUseCase,
    private val updateCurrentUser: UpdateCurrentUserUseCase,
    private val getDestination: GetAuthenticatedDestinationUseCase,
    private val userRepository: UserRepository
) : ViewModel(),
    StateHolder<RegisterState> by DefaultStateHolder(RegisterState()),
    EffectPublisher<RegisterEffect> by DefaultEffectPublisher() {

    private var backendGender: String? = null

    init {
        viewModelScope.launch {
            userRepository.refreshCurrentUser()
        }
        viewModelScope.launch {
            observeCurrentUser().collect { user ->
                if (user != null) {
                    backendGender = user.gender?.uppercase()?.takeIf(String::isNotBlank)
                    updateState {
                        copy(
                            firstName = user.firstName.orEmpty(),
                            lastName = user.lastName.orEmpty(),
                            dateOfBirth = user.dateOfBirth?.toDisplayDate().orEmpty(),
                            gender = backendGender ?: defaultGenderFor(mode),
                            profileImageUrl = user.profileImageUrl,
                            isInitializing = false,
                            errorMessage = null
                        )
                    }
                } else {
                    backendGender = null
                    updateState {
                        copy(
                            firstName = "",
                            lastName = "",
                            dateOfBirth = "",
                            gender = defaultGenderFor(mode),
                            profileImageUrl = null,
                            isInitializing = false
                        )
                    }
                }
            }
        }
    }

    fun onEvent(event: RegisterIntent) {
        when (event) {
            is RegisterIntent.ConfigureMode -> updateState {
                copy(
                    mode = event.mode,
                    gender = backendGender ?: defaultGenderFor(event.mode)
                )
            }
            is RegisterIntent.FirstNameChanged -> edit(ProfileField.FirstName) {
                copy(firstName = event.firstName.take(50))
            }
            is RegisterIntent.LastNameChanged -> edit(ProfileField.LastName) {
                copy(lastName = event.lastName.take(50))
            }
            is RegisterIntent.DateOfBirthChanged -> edit(ProfileField.DateOfBirth) {
                copy(dateOfBirth = event.dateOfBirth.take(10))
            }
            is RegisterIntent.GenderChanged -> edit(ProfileField.Gender) {
                copy(gender = event.gender)
            }
            is RegisterIntent.AvatarSelected -> updateState {
                copy(
                    avatarUri = event.uri,
                    selectedAvatarFileName = event.fileName,
                    selectedAvatarContentType = event.contentType,
                    selectedAvatarBytes = event.bytes
                )
            }
            RegisterIntent.EditAvatarClicked -> sendEffect(RegisterEffect.SelectAvatar)
            RegisterIntent.BackClicked -> sendEffect(RegisterEffect.NavigateBack)
            RegisterIntent.ContinueClicked -> submitPersonalInfo()
        }
    }

    private fun submitPersonalInfo() {
        if (currentState.isInitializing || currentState.isSubmitting) return
        val snapshot = currentState
        updateState { copy(isSubmitting = true, errorMessage = null) }
        viewModelScope.launch {
            val finalAvatarUrl = if (snapshot.selectedAvatarBytes != null && snapshot.selectedAvatarBytes.isNotEmpty()) {
                val uploadResult = userRepository.uploadProfileImage(
                    fileName = snapshot.selectedAvatarFileName ?: "profile.jpg",
                    contentType = snapshot.selectedAvatarContentType ?: "image/jpeg",
                    bytes = snapshot.selectedAvatarBytes
                )
                uploadResult.getOrElse { uploadError ->
                    android.util.Log.e("RegisterViewModel", "Avatar upload to Cloudinary failed", uploadError)
                    updateState {
                        copy(
                            isSubmitting = false,
                            errorMessage = uploadError.toAuthUiError(AuthUiError.PhotoUploadFailed)
                        )
                    }
                    return@launch
                }
            } else {
                snapshot.profileImageUrl
            }

            updateCurrentUser(
                firstName = snapshot.firstName,
                lastName = snapshot.lastName,
                dateOfBirth = snapshot.dateOfBirth,
                gender = snapshot.gender,
                profileImageUrl = finalAvatarUrl
            ).fold(
                onSuccess = { user ->
                    updateState {
                        copy(
                            isSubmitting = false,
                            validationErrors = emptyMap(),
                            errorMessage = null
                        )
                    }
                    if (snapshot.mode == PersonalInformationMode.EditProfile) {
                        sendEffect(RegisterEffect.NavigateAfterEdit)
                    } else {
                        getDestination(user).fold(
                            onSuccess = { destination ->
                                sendEffect(
                                    if (destination == AuthenticatedDestination.Home) {
                                        RegisterEffect.NavigateToHome
                                    } else {
                                        RegisterEffect.NavigateToWelcome(user.defaultProfileId)
                                    }
                                )
                            },
                            onFailure = { error ->
                                android.util.Log.e("RegisterViewModel", "Get destination failed", error)
                                updateState {
                                    copy(
                                        isSubmitting = false,
                                        errorMessage = error.toAuthUiError(AuthUiError.ProfileLoadFailed)
                                    )
                                }
                            }
                        )
                    }
                },
                onFailure = { error ->
                    android.util.Log.e("RegisterViewModel", "Update user profile failed", error)
                    val validation = error as? ProfileValidationException
                    updateState {
                        if (validation != null) {
                            copy(
                                isSubmitting = false,
                                validationErrors = validation.fieldErrors,
                                errorMessage = null
                            )
                        } else {
                            copy(
                                isSubmitting = false,
                                errorMessage = error.toAuthUiError(AuthUiError.ProfileSaveFailed)
                            )
                        }
                    }
                }
            )
        }
    }

    private fun edit(
        field: ProfileField,
        transform: RegisterState.() -> RegisterState
    ) = updateState {
        transform().copy(
            errorMessage = null,
            validationErrors = validationErrors - field
        )
    }
}

private fun defaultGenderFor(mode: PersonalInformationMode): String =
    if (mode == PersonalInformationMode.Registration) "MALE" else ""

private fun String.toDisplayDate(): String {
    if (isBlank()) return ""
    val trimmed = trim()
    if (trimmed.matches(Regex("^\\d{2}/\\d{2}/\\d{4}$"))) return trimmed
    val utc = java.util.TimeZone.getTimeZone("UTC")
    val parsers = listOf(
        "yyyy-MM-dd",
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss'Z'",
        "yyyy-MM-dd'T'HH:mm:ss"
    )
    for (pattern in parsers) {
        val parser = SimpleDateFormat(pattern, Locale.US).apply {
            isLenient = false
            timeZone = utc
        }
        val parsed = runCatching { parser.parse(trimmed) }.getOrNull()
        if (parsed != null) {
            val target = SimpleDateFormat("MM/dd/yyyy", Locale.US).apply { timeZone = utc }
            return target.format(parsed)
        }
    }
    return ""
}
