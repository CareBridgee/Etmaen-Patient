package com.carenest.domain.usecase.auth

import com.carenest.domain.repository.AuthRepository
import javax.inject.Inject

class RequestDevOtpUseCase @Inject constructor (
    private val authRepository: AuthRepository
) {
     suspend operator fun invoke(phoneNumber: String): Result<String?> {
        return authRepository.requestDevOtp(phoneNumber)
     }
}
