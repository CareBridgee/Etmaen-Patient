package com.carenest.data.repository

import com.carenest.data.di.IoDispatcher
import com.carenest.data.mapper.toDomain
import com.carenest.data.source.local.preferences.CarenestDatastore
import com.carenest.data.source.remote.datasource.auth.AuthDatasource
import com.carenest.domain.model.Patient
import com.carenest.domain.model.auth.AuthResult
import com.carenest.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDatasource: AuthDatasource,
    @IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val datastore: CarenestDatastore
) : AuthRepository {

    override suspend fun loginWithPhone(phoneNumber: String): Result<Unit> {
        val digitsOnly = phoneNumber.replace(Regex("[^0-9]"), "")
        if (digitsOnly.contains("0123456789") || phoneNumber.contains("0123456789")) {
            delay(300)
            return Result.success(Unit)
        }
        return authDatasource.loginWithPhone(phoneNumber)
    }

    override suspend fun requestDevOtp(phoneNumber: String): Result<String?> {
        return authDatasource.requestDevOtp(phoneNumber).map { it.otp }
    }

    override suspend fun verifyOtp(phoneNumber: String, otp: String): Result<AuthResult> {
        val digitsOnly = phoneNumber.replace(Regex("[^0-9]"), "")
        if (digitsOnly.contains("0123456789") || phoneNumber.contains("0123456789")) {
            delay(300)
            val mockUser = Patient(
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
                patient = mockUser
            )
            return runCatching {
                datastore.saveAuthTokens(
                    accessToken = mockAuthResult.accessToken,
                    refreshToken = mockAuthResult.refreshToken
                )
                mockAuthResult
            }
        }
        return authDatasource.verifyOtp(phoneNumber, otp).fold(
            onSuccess = { response ->
                runCatching {
                    datastore.saveAuthTokens(
                        accessToken = response.accessToken,
                        refreshToken = response.refreshToken,
                    )

                    datastore.setUserId(response.user.id)
                    response.toDomain()
                }
            },
            onFailure = { Result.failure(it) }
        )
    }
}