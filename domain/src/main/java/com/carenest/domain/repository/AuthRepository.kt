package com.carenest.domain.repository


import com.carenest.domain.model.auth.AuthResult

interface AuthRepository {
    suspend fun loginWithPhone(phoneNumber: String): Result<Unit>
    suspend fun requestDevOtp(phoneNumber: String): Result<String?>
    suspend fun verifyOtp(phoneNumber: String, otp: String): Result<AuthResult>
    suspend fun refreshToken(): Result<Unit>
}
