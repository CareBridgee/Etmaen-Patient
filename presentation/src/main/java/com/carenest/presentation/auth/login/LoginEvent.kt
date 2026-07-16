package com.carenest.presentation.auth.login

sealed interface LoginEvent {
    data class PhoneNumberChanged(val phone: String) : LoginEvent
    data class OtpCodeChanged(val otp: String) : LoginEvent
    data object ContinueWithPhoneClicked : LoginEvent
    data object RequestOtpClicked : LoginEvent
    data object VerifyOtpClicked : LoginEvent
    data object BackClicked : LoginEvent
}
