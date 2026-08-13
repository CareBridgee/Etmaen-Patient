package com.carenest.presentation.ui.auth.otp

import com.carenest.presentation.ui.auth.AuthUiError

sealed interface OtpIntent {
    data class OtpCodeChanged(val otp: String) : OtpIntent
    data class PhoneNumberChanged(val phone: String, val pendingToken: String? = null) : OtpIntent
    data object VerifyOtpClicked : OtpIntent
    data object BackClicked : OtpIntent
    data object ResendClicked : OtpIntent
    data object ConfirmSignInToExistingAccount : OtpIntent
    data object DismissExistingAccountDialog : OtpIntent
}

data class OtpState(
    val phoneNumber: String = "",
    val otpCode: String = "",
    val isLoading: Boolean = false,
    val remainingSeconds: Int = 30,
    val isResending: Boolean = false,
    val errorMessage: AuthUiError? = null,
    val pendingToken: String? = null,
    val existingAccountName: String? = null,
    val showExistingAccountDialog: Boolean = false
) {
    val canResend: Boolean get() = remainingSeconds == 0 && !isResending
}

sealed interface OtpEffect {
    data object NavigateToRegister : OtpEffect
    data object NavigateToCompleteProfile : OtpEffect
    data object NavigateToHome : OtpEffect
    data object NavigateBack : OtpEffect
}
