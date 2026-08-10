package com.carenest.presentation.ui.auth.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.domain.usecase.auth.LoginWithPhoneUseCase
import com.carenest.domain.usecase.auth.RequestDevOtpUseCase
import com.carenest.domain.validation.PhoneValidator
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginWithPhoneUseCase: LoginWithPhoneUseCase,
    private val requestDevOtpUseCase: RequestDevOtpUseCase
) : ViewModel(),
    StateHolder<LoginState> by DefaultStateHolder(LoginState()),
    EffectPublisher<LoginEffect> by DefaultEffectPublisher() {

    companion object {
        private const val TAG = "LoginViewModel"
    }

    fun onEvent(event: LoginIntent) {
        when (event) {
            is LoginIntent.PhoneNumberChanged -> {
                val phone = PhoneValidator.sanitize(event.phone, currentState.selectedCountry.phoneConfig)
                updateState {
                    copy(
                        phoneNumber = phone,
                        phoneValidationError = phone.takeIf(String::isNotBlank)?.let {
                            PhoneValidator.validate(it, selectedCountry.phoneConfig)
                        },
                        errorMessage = null
                    )
                }
            }

            is LoginIntent.OtpMethodChanged -> {
                updateState { copy(selectedOtpMethod = event.method) }
            }

            is LoginIntent.CountryCodeChanged -> {
                val phone = PhoneValidator.sanitize(currentState.phoneNumber, event.country.phoneConfig)
                updateState {
                    copy(
                        phoneNumber = phone,
                        selectedCountry = event.country,
                        isCountryDropdownExpanded = false,
                        phoneValidationError = phone.takeIf(String::isNotBlank)?.let {
                            PhoneValidator.validate(it, event.country.phoneConfig)
                        },
                        errorMessage = null
                    )
                }
            }

            LoginIntent.ToggleCountryDropdown -> {
                updateState { copy(isCountryDropdownExpanded = !isCountryDropdownExpanded) }
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
            LoginStep.PHONE_INPUT -> updateState {
                copy(
                    currentStep = LoginStep.LANDING,
                    phoneNumber = "",
                    phoneValidationError = null,
                    errorMessage = null
                )
            }

            LoginStep.LANDING -> {
                // Not handled here, screen level back or exit app
            }
        }
    }

    private fun requestOtp() {
        if (currentState.isLoading) return

        val validationError = PhoneValidator.validate(
            currentState.phoneNumber,
            currentState.selectedCountry.phoneConfig
        )
        if (validationError != null) {
            updateState { copy(phoneValidationError = validationError, errorMessage = null) }
            return
        }

        viewModelScope.launch {
            val fullPhoneNumber = PhoneValidator.toInternationalNumber(
                currentState.phoneNumber,
                currentState.selectedCountry.phoneConfig
            )
            Log.d(TAG, "requestOtp: $fullPhoneNumber")

            updateState { copy(isLoading = true, errorMessage = null) }

            val result = requestDevOtpUseCase(fullPhoneNumber)

            updateState { copy(isLoading = false) }

            result.fold(
                onSuccess = { otp ->
                    Log.d(TAG, "requestOtp success: $otp")
                    sendEffect(
                        LoginEffect.NavigateToOtp(
                            phone = fullPhoneNumber,
                            otp = otp,
                            method = currentState.selectedOtpMethod
                        )
                    )
                },
                onFailure = { error ->
                    Log.e(TAG, "requestOtp failed: ${error.message}", error)
                    updateState {
                        copy(
                            errorMessage = error.message ?: "Something went wrong. Please try again."
                        )
                    }
                }
            )
        }
    }
}
