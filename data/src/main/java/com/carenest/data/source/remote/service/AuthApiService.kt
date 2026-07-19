package com.carenest.data.source.remote.service


import com.carenest.data.source.remote.dto.AuthResponse

interface AuthApiService {
    suspend fun loginWithPhone(phoneNumber: String): Result<Unit>
    suspend fun verifyOtp(phoneNumber: String, otp: String): Result<AuthResponse>
}