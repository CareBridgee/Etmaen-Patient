package com.carenest.presentation.ui.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.domain.config.TemporaryCompleteProfileTestConfig
import com.carenest.domain.model.profile.ProfileField
import com.carenest.domain.model.profile.ProfileValidationException
import com.carenest.domain.usecase.profile.GetDefaultProfileUseCase
import com.carenest.domain.usecase.profile.UpdatePersonalInfoUseCase
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.launch

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
            RegisterIntent.BackClicked -> sendEffect(RegisterEffect.NavigateBack)
            RegisterIntent.ContinueClicked -> submitPersonalInfo()
        }
    }

    private fun submitPersonalInfo() {
        if (currentState.isInitializing || currentState.isSubmitting) return
        val profileId = currentState.profileId ?: return updateState {
            copy(errorMessage = "Profile information is unavailable. Please try again.")
        }
        val snapshot = currentState
        updateState { copy(isSubmitting = true, errorMessage = null) }
        viewModelScope.launch {
            updatePersonalInfo(
                profileId = profileId,
                firstName = snapshot.firstName,
                lastName = snapshot.lastName,
                dateOfBirth = snapshot.dateOfBirth,
                gender = snapshot.gender
            ).fold(
                onSuccess = {
                    updateState {
                        copy(isSubmitting = false, validationErrors = emptyMap(), errorMessage = null)
                    }
                    sendEffect(RegisterEffect.NavigateToWelcome)
                },
                onFailure = { error ->
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
                                errorMessage = error.message ?: "Unable to save personal information"
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

private fun String.toDisplayDate(): String = runCatching {
    val source = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { isLenient = false }
    val target = SimpleDateFormat("MM/dd/yyyy", Locale.US).apply { isLenient = false }
    target.format(source.parse(this)!!)
}.getOrDefault("")
