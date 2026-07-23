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
                copy(firstName = event.firstName, errorMessage = null)
            }
            is RegisterIntent.LastNameChanged -> updateState {
                copy(lastName = event.lastName, errorMessage = null)
            }
            is RegisterIntent.DateOfBirthChanged -> updateState {
                copy(dateOfBirth = event.dateOfBirth, errorMessage = null)
            }
            is RegisterIntent.GenderChanged -> updateState {
                copy(gender = event.gender, errorMessage = null)
            }
            RegisterIntent.BackClicked -> sendEffect(RegisterEffect.NavigateBack)
            RegisterIntent.ContinueClicked -> submitPersonalInfo()
        }
    }

    private fun submitPersonalInfo() {
        if (currentState.isInitializing || currentState.isSubmitting) return
        val profileId = currentState.profileId ?: return
        val firstName = currentState.firstName.trim()
        val lastName = currentState.lastName.trim()
        val dateOfBirth = currentState.dateOfBirth.toBackendDate()
        val gender = currentState.gender.takeIf { it in setOf("MALE", "FEMALE") }
        if (firstName.isBlank() || lastName.isBlank() || dateOfBirth == null || gender == null) {
            return updateState { copy(errorMessage = "Enter your name, date of birth, and gender") }
        }
        updateState { copy(isSubmitting = true, errorMessage = null) }
        viewModelScope.launch {
            updatePersonalInfo(
                profileId,
                PersonalInfoUpdate(firstName, lastName, dateOfBirth, gender)
            ).fold(
                onSuccess = {
                    updateState { copy(isSubmitting = false) }
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
