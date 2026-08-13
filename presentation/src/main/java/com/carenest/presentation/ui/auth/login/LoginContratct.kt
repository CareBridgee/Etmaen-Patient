package com.carenest.presentation.ui.auth.login

import com.carenest.domain.validation.PhoneNumberValidationError
import com.carenest.domain.validation.SupportedPhoneCountry
import com.carenest.presentation.ui.auth.AuthUiError

sealed interface LoginIntent {
    data class PhoneNumberChanged(val phone: String) : LoginIntent
    data class OtpMethodChanged(val method: OtpDeliveryMethod) : LoginIntent
    data class CountryCodeChanged(val country: Country) : LoginIntent
    data object ToggleCountryDropdown : LoginIntent
    data object ContinueWithPhoneClicked : LoginIntent
    data object RequestOtpClicked : LoginIntent
    data object BackClicked : LoginIntent
    data class GoogleSignInClicked(val idToken: String) : LoginIntent
    data class GoogleSignInFailed(val error: String) : LoginIntent
}

enum class LoginStep {
    LANDING,
    PHONE_INPUT
}

enum class OtpDeliveryMethod {
    SMS,
    WHATSAPP
}

data class Country(
    val name: String,
    val flag: String,
    val phoneConfig: SupportedPhoneCountry
) {
    val code: String get() = phoneConfig.dialCode
}

val countries = listOf(
    Country("Egypt", "\uD83C\uDDEA\uD83C\uDDEC", SupportedPhoneCountry.EGYPT),
    Country("Saudi Arabia", "\uD83C\uDDF8\uD83C\uDDE6", SupportedPhoneCountry.SAUDI_ARABIA),
    Country("UAE", "\uD83C\uDDE6\uD83C\uDDEA", SupportedPhoneCountry.UAE)
)

data class LoginState(
    val currentStep: LoginStep = LoginStep.LANDING,
    val phoneNumber: String = "",
    val selectedCountry: Country = countries[0],
    val isCountryDropdownExpanded: Boolean = false,
    val selectedOtpMethod: OtpDeliveryMethod = OtpDeliveryMethod.SMS,
    val isLoading: Boolean = false,
    val phoneValidationError: PhoneNumberValidationError? = null,
    val errorMessage: AuthUiError? = null,
    val pendingToken: String? = null,
    val email: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val profileImageUrl: String? = null
) {
    val isPhoneValid: Boolean
        get() = phoneNumber.isNotBlank() &&
            selectedCountry.phoneConfig.validate(phoneNumber) == null
}

sealed interface LoginEffect {
    data object NavigateToHome : LoginEffect
    data class NavigateToOtp(
        val phone: String,
        val otp: String? = null,
        val method: OtpDeliveryMethod,
        val pendingToken: String? = null
    ) : LoginEffect
}
