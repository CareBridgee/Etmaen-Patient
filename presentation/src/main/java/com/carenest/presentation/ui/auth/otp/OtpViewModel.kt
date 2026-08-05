package com.carenest.presentation.ui.auth.otp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.domain.usecase.auth.VerifyOtpUseCase
import com.carenest.domain.model.user.AuthenticatedDestination
import com.carenest.domain.usecase.user.GetAuthenticatedDestinationUseCase
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtpViewModel @Inject constructor(
    private val verifyOtpUseCase: VerifyOtpUseCase,
    private val getDestination: GetAuthenticatedDestinationUseCase
) : ViewModel(),
    StateHolder<OtpState> by DefaultStateHolder(OtpState()),
    EffectPublisher<OtpEffect> by DefaultEffectPublisher() {


    fun onEvent(event: OtpIntent) {
        when (event) {
            is OtpIntent.PhoneNumberChanged -> updateState { copy(phoneNumber = event.phone) }
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

            val digitsOnly = currentState.phoneNumber.replace(Regex("[^0-9]"), "")
            val sanitizedPhone = "+$digitsOnly"
            Log.d("OtpViewModel", "Verifying OTP for phone: $sanitizedPhone")
            
            val result = verifyOtpUseCase(sanitizedPhone, currentState.otpCode)
            
            result.fold(
                onSuccess = { authResult ->
                    getDestination(authResult.user).fold(
                        onSuccess = { destination ->
                            updateState { copy(isLoading = false) }
                            sendEffect(
                                when (destination) {
                                    AuthenticatedDestination.Registration -> OtpEffect.NavigateToRegister
                                    AuthenticatedDestination.CompleteProfile -> OtpEffect.NavigateToCompleteProfile
                                    AuthenticatedDestination.Home -> OtpEffect.NavigateToHome
                                }
                            )
                        },
                        onFailure = { error ->
                            updateState {
                                copy(
                                    isLoading = false,
                                    errorMessage = error.message ?: "Unable to load your profile"
                                )
                            }
                        }
                    )
                },
                onFailure = { error ->
                    updateState {
                        copy(isLoading = false, errorMessage = error.message ?: "Verification failed")
                    }
                }
            )
        }
    }
}
