package com.carenest.data.repository

import com.carenest.data.source.local.preferences.CarenestDatastore
import com.carenest.data.source.remote.datasource.auth.AuthDatasource
import com.carenest.domain.model.auth.AuthResult
import com.carenest.domain.repository.AuthRepository
import com.carenest.domain.repository.UserRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.authProvider
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDatasource: AuthDatasource,
    private val datastore: CarenestDatastore,
    private val userRepository: UserRepository,
    private val httpClient: HttpClient,
) : AuthRepository {

    override suspend fun loginWithPhone(phoneNumber: String): Result<Unit> =
        authDatasource.loginWithPhone(phoneNumber)

    override suspend fun requestDevOtp(phoneNumber: String): Result<String?> {
        return authDatasource.requestDevOtp(phoneNumber).map { it.otp }
    }

    override suspend fun verifyOtp(phoneNumber: String, otp: String): Result<AuthResult> {
        return authDatasource.verifyOtp(phoneNumber, otp).fold(
            onSuccess = { response ->
                runCatching {
                    datastore.saveAuthTokens(
                        accessToken = response.accessToken,
                        refreshToken = response.refreshToken,
                    )

                    clearBearerTokenCache()

                    val user = userRepository.refreshCurrentUser().getOrThrow()
                    datastore.setUserId(user.id)
                    datastore.setLoggedIn(true)
                    AuthResult(
                        accessToken = response.accessToken,
                        refreshToken = response.refreshToken,
                        expiresIn = response.expiresIn,
                        user = user
                    )
                }.onFailure {
                    datastore.clearAuthTokens()
                    clearBearerTokenCache()
                    userRepository.clearCurrentUser()
                    datastore.setLoggedIn(false)
                }
            },
            onFailure = { Result.failure(it) }
        )
    }

    override suspend fun logout(): Result<Unit> = runCatching {
        datastore.clearAuthTokens()
        clearBearerTokenCache()
        userRepository.clearCurrentUser()
        datastore.setLoggedIn(false)
    }
    private fun clearBearerTokenCache() {
        httpClient
            .authProvider<BearerAuthProvider>()
            ?.clearToken()
    }
}