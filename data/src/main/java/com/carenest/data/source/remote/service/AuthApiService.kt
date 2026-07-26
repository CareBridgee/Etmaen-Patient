package com.carenest.data.source.remote.service


import com.carenest.data.source.remote.dto.AuthResponse
import com.carenest.data.source.remote.dto.LoginResponse

interface AuthApiService {
    suspend fun loginWithPhone(phoneNumber: String): Result<Unit>
    suspend fun requestDevOtp(phoneNumber: String): Result<LoginResponse>
    suspend fun verifyOtp(phoneNumber: String, otp: String): Result<AuthResponse>
}