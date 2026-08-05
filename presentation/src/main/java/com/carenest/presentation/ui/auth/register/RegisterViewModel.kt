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
import kotlinx.coroutines.launch

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val observeCurrentUser: ObserveCurrentUserUseCase,
    private val updateCurrentUser: UpdateCurrentUserUseCase,
    private val getDestination: GetAuthenticatedDestinationUseCase
) : ViewModel(),
    StateHolder<RegisterState> by DefaultStateHolder(RegisterState()),
    EffectPublisher<RegisterEffect> by DefaultEffectPublisher() {

    init {
        viewModelScope.launch {
            val user = observeCurrentUser().first()
            if (user == null) {
                updateState {
                    copy(isInitializing = false, errorMessage = "User information is unavailable")
                }
            } else {
                updateState {
                    copy(
                        firstName = user.firstName.orEmpty(),
                        lastName = user.lastName.orEmpty(),
                        dateOfBirth = user.dateOfBirth?.toDisplayDate().orEmpty(),
                        gender = user.gender?.uppercase().orEmpty(),
                        isInitializing = false
                    )
                }
            }
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
        val snapshot = currentState
        updateState { copy(isSubmitting = true, errorMessage = null) }
        viewModelScope.launch {
            updateCurrentUser(
                firstName = snapshot.firstName,
                lastName = snapshot.lastName,
                dateOfBirth = snapshot.dateOfBirth,
                gender = snapshot.gender
            ).fold(
                onSuccess = { user ->
                    getDestination(user).fold(
                        onSuccess = { destination ->
                            updateState {
                                copy(
                                    isSubmitting = false,
                                    validationErrors = emptyMap(),
                                    errorMessage = null
                                )
                            }
                            sendEffect(
                                if (destination == AuthenticatedDestination.Home) {
                                    RegisterEffect.NavigateToHome
                                } else {
                                    RegisterEffect.NavigateToWelcome
                                }
                            )
                        },
                        onFailure = { error ->
                            updateState {
                                copy(
                                    isSubmitting = false,
                                    errorMessage = error.message ?: "Unable to load your profile"
                                )
                            }
                        }
                    )
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
