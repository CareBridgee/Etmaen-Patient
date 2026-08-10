package com.carenest.presentation.ui.auth.otp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.domain.usecase.auth.RequestDevOtpUseCase
import com.carenest.domain.usecase.auth.VerifyOtpUseCase
import com.carenest.domain.model.user.AuthenticatedDestination
import com.carenest.domain.usecase.user.GetAuthenticatedDestinationUseCase
import com.carenest.domain.validation.PhoneValidator
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import com.carenest.presentation.ui.auth.AuthUiError
import com.carenest.presentation.ui.auth.toAuthUiError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtpViewModel @Inject constructor(
    private val verifyOtpUseCase: VerifyOtpUseCase,
    private val getDestination: GetAuthenticatedDestinationUseCase,
    private val requestDevOtpUseCase: RequestDevOtpUseCase
) : ViewModel(),
    StateHolder<OtpState> by DefaultStateHolder(OtpState()),
    EffectPublisher<OtpEffect> by DefaultEffectPublisher() {

    private var countdownJob: Job? = null

    init {
        startCountdown()
    }

    fun onEvent(event: OtpIntent) {
        when (event) {
            is OtpIntent.PhoneNumberChanged -> updateState { copy(phoneNumber = event.phone) }
            is OtpIntent.OtpCodeChanged -> updateState { copy(otpCode = event.otp, errorMessage = null) }
            OtpIntent.VerifyOtpClicked -> verifyOtp()
            OtpIntent.BackClicked -> sendEffect(OtpEffect.NavigateBack)
            OtpIntent.ResendClicked -> resendOtp()
        }
    }

    private fun startCountdown() {
        countdownJob?.cancel()
        updateState { copy(remainingSeconds = RESEND_SECONDS) }
        countdownJob = viewModelScope.launch {
            for (seconds in (RESEND_SECONDS - 1) downTo 0) {
                delay(ONE_SECOND_MILLIS)
                updateState { copy(remainingSeconds = seconds) }
            }
        }
    }

    private fun resendOtp() {
        if (!currentState.canResend) return
        val phoneNumber = PhoneValidator.normalizeInternationalNumber(currentState.phoneNumber)
        if (phoneNumber == null) {
            updateState { copy(errorMessage = AuthUiError.InvalidPhone) }
            return
        }

        updateState { copy(isResending = true, errorMessage = null) }
        viewModelScope.launch {
            val result = requestDevOtpUseCase(phoneNumber)
            result.fold(
                onSuccess = { otp ->
                    updateState {
                        copy(
                            otpCode = otp.orEmpty(),
                            isResending = false,
                            errorMessage = null
                        )
                    }
                    startCountdown()
                },
                onFailure = { error ->
                    updateState {
                        copy(
                            isResending = false,
                            remainingSeconds = 0,
                            errorMessage = error.toAuthUiError(AuthUiError.ResendCodeFailed)
                        )
                    }
                }
            )
        }
    }

    private fun verifyOtp() {
        if (currentState.otpCode.length != 6) {
            updateState { copy(errorMessage = AuthUiError.OtpIncomplete) }
            return
        }

        viewModelScope.launch {
            updateState { copy(isLoading = true, errorMessage = null) }

            val sanitizedPhone = PhoneValidator.normalizeInternationalNumber(currentState.phoneNumber)
                ?: currentState.phoneNumber
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
                                    errorMessage = error.toAuthUiError(AuthUiError.ProfileLoadFailed)
                                )
                            }
                        }
                    )
                },
                onFailure = { error ->
                    updateState {
                        copy(
                            isLoading = false,
                            errorMessage = error.toAuthUiError(AuthUiError.VerificationFailed)
                        )
                    }
                }
            )
        }
    }

    private companion object {
        const val RESEND_SECONDS = 30
        const val ONE_SECOND_MILLIS = 1_000L
    }
}
