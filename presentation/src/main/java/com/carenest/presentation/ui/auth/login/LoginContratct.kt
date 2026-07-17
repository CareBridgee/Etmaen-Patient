package com.carenest.presentation.ui.auth.login

sealed interface LoginIntent {
    data class PhoneNumberChanged(val phone: String) : LoginIntent
    data class OtpMethodChanged(val method: OtpDeliveryMethod) : LoginIntent
    data object ContinueWithPhoneClicked : LoginIntent
    data object RequestOtpClicked : LoginIntent
    data object BackClicked : LoginIntent
}

enum class LoginStep {
    LANDING,
    PHONE_INPUT
}

enum class OtpDeliveryMethod {
    SMS,
    WHATSAPP
}

data class LoginState(
    val currentStep: LoginStep = LoginStep.LANDING,
    val phoneNumber: String = "",
    val selectedOtpMethod: OtpDeliveryMethod = OtpDeliveryMethod.SMS,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed interface LoginEffect {
    data class NavigateToOtp(val phone: String, val method: OtpDeliveryMethod) : LoginEffect
}
