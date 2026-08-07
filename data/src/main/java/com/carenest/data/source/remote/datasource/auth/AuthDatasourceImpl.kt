package com.carenest.data.source.remote.datasource.auth

import com.carenest.data.source.remote.dto.AuthResponse
import com.carenest.data.source.remote.dto.LoginResponse
import com.carenest.data.source.remote.dto.TokenPairResponse
import com.carenest.data.source.remote.service.AuthApiService
import javax.inject.Inject


class AuthDatasourceImpl @Inject constructor(
    private val authApiService: AuthApiService
): AuthDatasource {
    override suspend fun loginWithPhone(phoneNumber: String): Result<Unit> {
        return authApiService.loginWithPhone(phoneNumber)
    }

    override suspend fun requestDevOtp(phoneNumber: String): Result<LoginResponse> {
        return authApiService.requestDevOtp(phoneNumber)
    }

    override suspend fun verifyOtp(phoneNumber: String, otp: String): Result<AuthResponse> {
        return authApiService.verifyOtp(phoneNumber, otp)
    }

    override suspend fun refreshToken(refreshToken: String): Result<TokenPairResponse> {
        return authApiService.refreshToken(refreshToken)
    }
}
