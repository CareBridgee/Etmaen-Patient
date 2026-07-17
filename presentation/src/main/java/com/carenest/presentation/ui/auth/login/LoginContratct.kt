package com.carenest.presentation.ui.auth.login

sealed interface LoginIntent {
    data class PhoneNumberChanged(val phone: String) : LoginIntent
    data class OtpCodeChanged(val otp: String) : LoginIntent
    data object ContinueWithPhoneClicked : LoginIntent
    data object RequestOtpClicked : LoginIntent
    data object VerifyOtpClicked : LoginIntent
    data object BackClicked : LoginIntent
}


enum class LoginStep {
    LANDING,
    PHONE_INPUT,
    VERIFY_OTP
}

data class LoginState(
    val currentStep: LoginStep = LoginStep.LANDING,
    val phoneNumber: String = "",
    val otpCode: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)
