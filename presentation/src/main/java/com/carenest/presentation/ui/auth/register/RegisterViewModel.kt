package com.carenest.presentation.ui.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.domain.model.profile.PersonalInfoUpdate
import com.carenest.domain.config.TemporaryCompleteProfileTestConfig
import com.carenest.domain.usecase.profile.GetDefaultProfileUseCase
import com.carenest.domain.usecase.profile.UpdatePersonalInfoUseCase
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import com.carenest.presentation.ui.profile.validation.ProfileField
import com.carenest.presentation.ui.profile.validation.ProfileInputValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val getDefaultProfile: GetDefaultProfileUseCase,
    private val updatePersonalInfo: UpdatePersonalInfoUseCase
) : ViewModel(),
    StateHolder<RegisterState> by DefaultStateHolder(RegisterState()),
    EffectPublisher<RegisterEffect> by DefaultEffectPublisher() {

    init {
        viewModelScope.launch {
            if (TemporaryCompleteProfileTestConfig.ENABLED) {
                updateState {
                    copy(
                        profileId = TemporaryCompleteProfileTestConfig.PROFILE_ID,
                        isInitializing = false
                    )
                }
                return@launch
            }
            getDefaultProfile().fold(
                onSuccess = { profile ->
                    updateState {
                        copy(
                            profileId = profile.id,
                            firstName = profile.firstName.orEmpty(),
                            lastName = profile.lastName.orEmpty(),
                            dateOfBirth = profile.dateOfBirth?.toDisplayDate().orEmpty(),
                            gender = profile.gender?.uppercase().orEmpty(),
                            isInitializing = false
                        )
                    }
                },
                onFailure = {
                    updateState {
                        copy(isInitializing = false, errorMessage = it.message ?: "Unable to load your profile")
                    }
                }
            )
        }
    }

    fun onEvent(event: RegisterIntent) {
        when (event) {
            is RegisterIntent.FirstNameChanged -> updateState {
                copy(
                    firstName = event.firstName.take(50),
                    errorMessage = null,
                    validationErrors = validationErrors - ProfileField.FirstName
                )
            }
            is RegisterIntent.LastNameChanged -> updateState {
                copy(
                    lastName = event.lastName.take(50),
                    errorMessage = null,
                    validationErrors = validationErrors - ProfileField.LastName
                )
            }
            is RegisterIntent.DateOfBirthChanged -> updateState {
                copy(
                    dateOfBirth = event.dateOfBirth.take(10),
                    errorMessage = null,
                    validationErrors = validationErrors - ProfileField.DateOfBirth
                )
            }
            is RegisterIntent.GenderChanged -> updateState {
                copy(
                    gender = event.gender,
                    errorMessage = null,
                    validationErrors = validationErrors - ProfileField.Gender
                )
            }
            RegisterIntent.BackClicked -> sendEffect(RegisterEffect.NavigateBack)
            RegisterIntent.ContinueClicked -> submitPersonalInfo()
        }
    }

    private fun submitPersonalInfo() {
        if (currentState.isInitializing || currentState.isSubmitting) return
        val profileId = currentState.profileId ?: return updateState {
            copy(errorMessage = "Profile information is unavailable. Please try again.")
        }
        val validationErrors = ProfileInputValidator.personalInfo(
            currentState.firstName,
            currentState.lastName,
            currentState.dateOfBirth,
            currentState.gender
        )
        if (validationErrors.isNotEmpty()) {
            return updateState {
                copy(validationErrors = validationErrors, errorMessage = null)
            }
        }

        val firstName = currentState.firstName.trim()
        val lastName = currentState.lastName.trim()
        val dateOfBirth = requireNotNull(currentState.dateOfBirth.toBackendDate())
        val gender = currentState.gender
        updateState { copy(isSubmitting = true, errorMessage = null) }
        viewModelScope.launch {
            updatePersonalInfo(
                profileId,
                PersonalInfoUpdate(firstName, lastName, dateOfBirth, gender)
            ).fold(
                onSuccess = {
                    updateState {
                        copy(
                            isSubmitting = false,
                            validationErrors = emptyMap(),
                            errorMessage = null
                        )
                    }
                    sendEffect(RegisterEffect.NavigateToWelcome)
                },
                onFailure = {
                    updateState {
                        copy(isSubmitting = false, errorMessage = it.message ?: "Unable to save personal information")
                    }
                }
            )
        }
    }
}

private fun String.toBackendDate(): String? =
    convertDate(this, "MM/dd/yyyy", "yyyy-MM-dd")

private fun String.toDisplayDate(): String =
    convertDate(this, "yyyy-MM-dd", "MM/dd/yyyy").orEmpty()

private fun convertDate(value: String, from: String, to: String): String? = runCatching {
    val source = SimpleDateFormat(from, Locale.US).apply { isLenient = false }
    val target = SimpleDateFormat(to, Locale.US).apply { isLenient = false }
    target.format(source.parse(value)!!)
}.getOrNull()
