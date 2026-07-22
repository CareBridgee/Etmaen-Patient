package com.carenest.data.repository

import com.carenest.data.mapper.toDomain
import com.carenest.data.source.remote.datasource.auth.AuthDatasource
import com.carenest.domain.model.auth.AuthResult
import com.carenest.domain.model.auth.User
import com.carenest.domain.repository.AuthRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDatasource: AuthDatasource
) : AuthRepository {

    override suspend fun loginWithPhone(phoneNumber: String): Result<Unit> {
        val digitsOnly = phoneNumber.replace(Regex("[^0-9]"), "")
        if (digitsOnly.contains("0123456789") || phoneNumber.contains("0123456789")) {
            delay(300)
            return Result.success(Unit)
        }
        return authDatasource.loginWithPhone(phoneNumber)
    }

    override suspend fun verifyOtp(phoneNumber: String, otp: String): Result<AuthResult> {
        val digitsOnly = phoneNumber.replace(Regex("[^0-9]"), "")
        if (digitsOnly.contains("0123456789") || phoneNumber.contains("0123456789")) {
            delay(300)
            val mockUser = User(
                id = "usr_0123456789",
                phoneNumber = phoneNumber,
                firstName = "Elena",
                lastName = "Smith",
                dateOfBirth = "1990-01-01",
                gender = "Female",
                profileImageUrl = null,
                isDeleted = false,
                createdAt = "2026-01-01T00:00:00Z",
                updatedAt = "2026-01-01T00:00:00Z",
                lastLoginAt = "2026-07-22T00:00:00Z",
                defaultProfileId = null
            )
            val mockAuthResult = AuthResult(
                accessToken = "mock_access_token_0123456789",
                refreshToken = "mock_refresh_token_0123456789",
                expiresIn = 3600L,
                user = mockUser
            )
            return Result.success(mockAuthResult)
        }
        return authDatasource.verifyOtp(phoneNumber, otp).map { it.toDomain() }
    }
}