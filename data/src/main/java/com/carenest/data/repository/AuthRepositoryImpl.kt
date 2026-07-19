package com.carenest.data.repository

import com.carenest.data.mapper.toDomain
import com.carenest.data.source.remote.datasource.auth.AuthDatasource
import com.carenest.domain.model.auth.AuthResult
import com.carenest.domain.repository.AuthRepository
import javax.inject.Inject


class AuthRepositoryImpl @Inject constructor (
    private val authDatasource: AuthDatasource
): AuthRepository {
    override suspend fun loginWithPhone(phoneNumber: String): Result<Unit> {
        return authDatasource.loginWithPhone(phoneNumber)
    }

    override suspend fun verifyOtp(phoneNumber: String, otp: String): Result<AuthResult> {
        return authDatasource.verifyOtp(phoneNumber, otp).map { it.toDomain() }
    }
}