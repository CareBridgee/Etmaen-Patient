package com.carenest.domain.repository


import com.carenest.domain.model.auth.AuthResult
import com.carenest.domain.model.auth.GoogleAuthResult

interface AuthRepository {
    suspend fun loginWithPhone(phoneNumber: String): Result<Unit>
    suspend fun loginWithGoogle(idToken: String): Result<GoogleAuthResult>
    suspend fun requestDevOtp(phoneNumber: String): Result<String?>
    suspend fun verifyOtp(phoneNumber: String, otp: String, pendingToken: String? = null): Result<AuthResult>
    suspend fun refreshToken(): Result<Unit>
    suspend fun logout(): Result<Unit>
}
