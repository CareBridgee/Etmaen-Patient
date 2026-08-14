package com.carenest.domain.usecase.auth

import com.carenest.domain.model.auth.GoogleAuthResult
import com.carenest.domain.repository.AuthRepository
import javax.inject.Inject

class LoginWithGoogleUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(idToken: String): Result<GoogleAuthResult> {
        return authRepository.loginWithGoogle(idToken)
    }
}
