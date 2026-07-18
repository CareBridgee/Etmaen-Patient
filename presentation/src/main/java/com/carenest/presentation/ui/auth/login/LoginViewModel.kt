package com.carenest.presentation.ui.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel(),
    StateHolder<LoginState> by DefaultStateHolder(LoginState()),
    EffectPublisher<LoginEffect> by DefaultEffectPublisher() {

    fun onEvent(event: LoginIntent) {
        when (event) {
            is LoginIntent.PhoneNumberChanged -> {
                updateState { copy(phoneNumber = event.phone, errorMessage = null) }
            }
            is LoginIntent.OtpMethodChanged -> {
                updateState { copy(selectedOtpMethod = event.method) }
            }
            LoginIntent.ContinueWithPhoneClicked -> {
                updateState { copy(currentStep = LoginStep.PHONE_INPUT) }
            }
            LoginIntent.RequestOtpClicked -> requestOtp()
            LoginIntent.BackClicked -> handleBack()
        }
    }

    private fun handleBack() {
        when (currentState.currentStep) {
            LoginStep.PHONE_INPUT -> updateState { copy(currentStep = LoginStep.LANDING, phoneNumber = "") }
            LoginStep.LANDING -> {
                // Not handled here, screen level back or exit app
            }
        }
    }

    private fun requestOtp() {
        if (currentState.phoneNumber.isBlank()) {
            updateState { copy(errorMessage = "Phone number is required") }
            return
        }
        
        viewModelScope.launch {
            updateState { copy(isLoading = true, errorMessage = null) }
            delay(1500) // Mock network delay
            updateState { 
                copy(
                    isLoading = false
                ) 
            }
            // Emit effect to navigate to verify screen
            sendEffect(LoginEffect.NavigateToOtp(currentState.phoneNumber, currentState.selectedOtpMethod))
        }
    }
}
