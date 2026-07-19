package com.carenest.domain.usecase.auth

import com.carenest.domain.model.auth.AuthResult
import com.carenest.domain.repository.AuthRepository
import javax.inject.Inject

class VerifyOtpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(phoneNumber: String, otp: String): Result<AuthResult> {
        return authRepository.verifyOtp(phoneNumber, otp)
    }
}
