package com.carenest.domain.usecase.auth

import com.carenest.domain.repository.AuthRepository
import javax.inject.Inject


class LoginWithPhoneUseCase @Inject constructor (
    private val authRepository: AuthRepository
) {
     suspend operator fun invoke(phoneNumber: String): Result<Unit> {
        return authRepository.loginWithPhone(phoneNumber)
     }
}
