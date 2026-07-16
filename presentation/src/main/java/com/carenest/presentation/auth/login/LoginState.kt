package com.carenest.presentation.auth.login

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
