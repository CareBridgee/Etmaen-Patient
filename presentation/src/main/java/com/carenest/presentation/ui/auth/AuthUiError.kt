package com.carenest.presentation.ui.auth

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.carenest.domain.model.auth.AuthException
import com.carenest.domain.model.auth.AuthFailure
import com.carenest.domain.model.user.UserException
import com.carenest.presentation.R
import java.io.IOException

enum class AuthUiError(@get:StringRes val messageRes: Int) {
    InvalidPhone(R.string.auth_error_invalid_phone),
    OtpIncomplete(R.string.auth_error_otp_incomplete),
    InvalidOtp(R.string.auth_error_invalid_otp),
    ExpiredOtp(R.string.auth_error_expired_otp),
    NetworkUnavailable(R.string.auth_error_network),
    TooManyRequests(R.string.auth_error_too_many_requests),
    ServiceUnavailable(R.string.auth_error_service_unavailable),
    SendCodeFailed(R.string.auth_error_send_code),
    ResendCodeFailed(R.string.auth_error_resend_code),
    VerificationFailed(R.string.auth_error_verification),
    ProfileLoadFailed(R.string.auth_error_profile_load),
    PhotoUploadFailed(R.string.auth_error_photo_upload),
    ProfileSaveFailed(R.string.auth_error_profile_save),
    GoogleSignInFailed(R.string.auth_error_google_sign_in_failed),
    PhoneAlreadyTaken(R.string.auth_error_phone_already_taken)
}

@Composable
fun AuthUiError?.localizedMessage(): String? = this?.let { stringResource(it.messageRes) }

fun Throwable.toAuthUiError(default: AuthUiError): AuthUiError = when (this) {
    is AuthException -> when (failure) {
        AuthFailure.Network -> AuthUiError.NetworkUnavailable
        AuthFailure.InvalidPhone -> AuthUiError.InvalidPhone
        AuthFailure.InvalidOtp -> AuthUiError.InvalidOtp
        AuthFailure.ExpiredOtp -> AuthUiError.ExpiredOtp
        AuthFailure.TooManyRequests -> AuthUiError.TooManyRequests
        AuthFailure.Server -> AuthUiError.ServiceUnavailable
        AuthFailure.PhoneAlreadyTaken -> AuthUiError.PhoneAlreadyTaken
        AuthFailure.Unknown -> default
    }
    is UserException -> when {
        statusCode == 429 -> AuthUiError.TooManyRequests
        (statusCode ?: 0) >= 500 -> AuthUiError.ServiceUnavailable
        else -> default
    }
    is IOException -> AuthUiError.NetworkUnavailable
    else -> default
}
