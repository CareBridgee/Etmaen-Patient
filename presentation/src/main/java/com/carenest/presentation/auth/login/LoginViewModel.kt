package com.carenest.presentation.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel(),
    StateHolder<LoginState> by DefaultStateHolder(LoginState()),
    EffectPublisher<Unit> by DefaultEffectPublisher() {

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.PhoneNumberChanged -> {
                updateState { copy(phoneNumber = event.phone, errorMessage = null) }
            }
            is LoginEvent.OtpCodeChanged -> {
                updateState { copy(otpCode = event.otp, errorMessage = null) }
            }
            LoginEvent.ContinueWithPhoneClicked -> {
                updateState { copy(currentStep = LoginStep.PHONE_INPUT) }
            }
            LoginEvent.RequestOtpClicked -> requestOtp()
            LoginEvent.VerifyOtpClicked -> verifyOtp()
            LoginEvent.BackClicked -> handleBack()
        }
    }

    private fun handleBack() {
        when (currentState.currentStep) {
            LoginStep.VERIFY_OTP -> updateState { copy(currentStep = LoginStep.PHONE_INPUT, otpCode = "") }
            LoginStep.PHONE_INPUT -> updateState { copy(currentStep = LoginStep.LANDING, phoneNumber = "") }
            LoginStep.LANDING -> {
                // Not handled here, screen level back
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
                    isLoading = false,
                    currentStep = LoginStep.VERIFY_OTP 
                ) 
            }
        }
    }

    private fun verifyOtp() {
        if (currentState.otpCode.length != 6) {
            updateState { copy(errorMessage = "Invalid OTP code") }
            return
        }

        viewModelScope.launch {
            updateState { copy(isLoading = true, errorMessage = null) }
            delay(1500) // Mock network delay
            
            if (currentState.otpCode == "123456") {
                updateState { 
                    copy(
                        isLoading = false,
                        isSuccess = true
                    ) 
                }
                sendEffect(Unit) // Trigger navigation
            } else {
                updateState { 
                    copy(
                        isLoading = false,
                        errorMessage = "Invalid OTP code. Try 123456"
                    ) 
                }
            }
        }
    }
}
