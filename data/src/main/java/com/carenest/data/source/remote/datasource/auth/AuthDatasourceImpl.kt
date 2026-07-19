package com.carenest.data.source.remote.datasource.auth

import com.carenest.data.source.remote.dto.AuthResponse
import com.carenest.data.source.remote.service.AuthApiService
import javax.inject.Inject


class AuthDatasourceImpl @Inject constructor(
    private val authApiService: AuthApiService
): AuthDatasource {
    override suspend fun loginWithPhone(phoneNumber: String): Result<Unit> {
        return authApiService.loginWithPhone(phoneNumber)
    }

    override suspend fun verifyOtp(phoneNumber: String, otp: String): Result<AuthResponse> {
        return authApiService.verifyOtp(phoneNumber, otp)
    }
}
