package com.carenest.presentation.ui.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel(),
    StateHolder<RegisterState> by DefaultStateHolder(RegisterState()),
    EffectPublisher<Unit> by DefaultEffectPublisher() {

    fun onEvent(event: RegisterIntent) {
        when (event) {
            is RegisterIntent.FirstNameChanged -> updateState { copy(firstName = event.firstName, errorMessage = null) }
            is RegisterIntent.LastNameChanged -> updateState { copy(lastName = event.lastName, errorMessage = null) }
            is RegisterIntent.DobChanged -> updateState { copy(dateOfBirth = event.dob, errorMessage = null) }
            is RegisterIntent.GenderChanged -> updateState { copy(gender = event.gender, errorMessage = null) }
            RegisterIntent.CompleteProfileClicked -> updateState { copy(currentStep = RegisterStep.PERSONAL_INFO) }
            RegisterIntent.SkipClicked -> sendEffect(Unit) // Skip and go to home
            RegisterIntent.ContinueClicked -> submitProfile()
            RegisterIntent.BackClicked -> handleBack()
        }
    }

    private fun handleBack() {
        if (currentState.currentStep == RegisterStep.PERSONAL_INFO) {
            updateState { copy(currentStep = RegisterStep.WELCOME) }
        }
    }

    private fun submitProfile() {
        if (currentState.firstName.isBlank() || currentState.lastName.isBlank()) {
            updateState { copy(errorMessage = "First and Last name are required") }
            return
        }

        viewModelScope.launch {
            updateState { copy(isLoading = true, errorMessage = null) }
            delay(1500) // Mock network delay
            updateState { 
                copy(
                    isLoading = false,
                    isSuccess = true
                ) 
            }
            sendEffect(Unit)
        }
    }
}
