package com.carenest.presentation.ui.auth.otp

import androidx.lifecycle.SavedStateHandle
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
class OtpViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel(),
    StateHolder<OtpState> by DefaultStateHolder(OtpState()),
    EffectPublisher<OtpEffect> by DefaultEffectPublisher() {

    init {
        // Assume phone number is passed via navigation
        val phone = savedStateHandle.get<String>("phone") ?: ""
        updateState { copy(phoneNumber = phone) }
    }

    fun onEvent(event: OtpIntent) {
        when (event) {
            is OtpIntent.OtpCodeChanged -> updateState { copy(otpCode = event.otp, errorMessage = null) }
            OtpIntent.VerifyOtpClicked -> verifyOtp()
            OtpIntent.BackClicked -> sendEffect(OtpEffect.NavigateBack)
            OtpIntent.ResendClicked -> { /* TODO: Resend OTP */ }
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
                    copy(isLoading = false, isSuccess = true) 
                }
                sendEffect(OtpEffect.NavigateToHome)
            } else {
                updateState { 
                    copy(isLoading = false, errorMessage = "Invalid OTP code. Try 123456") 
                }
            }
        }
    }
}
