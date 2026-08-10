package com.carenest.presentation.ui.auth

import com.carenest.domain.model.auth.AuthException
import com.carenest.domain.model.auth.AuthFailure
import com.carenest.domain.model.user.UserException
import java.io.IOException
import org.junit.Assert.assertEquals
import org.junit.Test

class AuthUiErrorTest {

    @Test
    fun authFailuresMapToSpecificUserFacingErrors() {
        val cases = listOf(
            AuthFailure.Network to AuthUiError.NetworkUnavailable,
            AuthFailure.InvalidPhone to AuthUiError.InvalidPhone,
            AuthFailure.InvalidOtp to AuthUiError.InvalidOtp,
            AuthFailure.ExpiredOtp to AuthUiError.ExpiredOtp,
            AuthFailure.TooManyRequests to AuthUiError.TooManyRequests,
            AuthFailure.Server to AuthUiError.ServiceUnavailable,
            AuthFailure.Unknown to AuthUiError.VerificationFailed
        )

        cases.forEach { (failure, expected) ->
            val error = AuthException(failure, "technical backend message")
            assertEquals(expected, error.toAuthUiError(AuthUiError.VerificationFailed))
        }
    }

    @Test
    fun networkAndUserHttpErrorsMapWithoutExposingRawMessages() {
        assertEquals(
            AuthUiError.NetworkUnavailable,
            IOException("socket details").toAuthUiError(AuthUiError.ProfileSaveFailed)
        )
        assertEquals(
            AuthUiError.TooManyRequests,
            UserException("backend details", statusCode = 429)
                .toAuthUiError(AuthUiError.ProfileSaveFailed)
        )
        assertEquals(
            AuthUiError.ServiceUnavailable,
            UserException("backend details", statusCode = 503)
                .toAuthUiError(AuthUiError.ProfileSaveFailed)
        )
    }
}
