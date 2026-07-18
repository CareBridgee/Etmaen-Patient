package com.carenest.data.source.remote.datasource.auth


import com.carenest.data.source.remote.dto.AuthResponse

interface AuthDatasource {
    suspend fun loginWithPhone(phoneNumber: String): Result<Unit>
    suspend fun verifyOtp(phoneNumber: String, otp: String): Result<AuthResponse>
}
