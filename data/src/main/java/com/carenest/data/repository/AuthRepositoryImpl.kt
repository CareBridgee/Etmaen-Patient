package com.carenest.data.repository

import com.carenest.data.source.local.preferences.CarenestDatastore
import com.carenest.data.source.remote.datasource.auth.AuthDatasource
import com.carenest.domain.model.auth.AuthResult
import com.carenest.domain.repository.AuthRepository
import com.carenest.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDatasource: AuthDatasource,
    private val datastore: CarenestDatastore,
    private val userRepository: UserRepository,
    @param:IoDispatcher private val dispatcher: CoroutineDispatcher
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
                    userRepository.clearCurrentUser()
                    datastore.setLoggedIn(false)
                }
            },
            onFailure = { Result.failure(it) }
        )
    }

    override suspend fun refreshToken(): Result<Unit> = withContext(dispatcher) {
        val tokens = datastore.authTokens.first()
        val refreshToken = tokens?.refreshToken ?: return@withContext Result.failure(Exception("No refresh token"))

        authDatasource.refreshToken(refreshToken).fold(
            onSuccess = { response ->
                if(response.refreshToken?.isBlank() == true || response.accessToken?.isBlank() == true){
                    datastore.clearAuthTokens()
                    datastore.setLoggedIn(false)
                    return@withContext Result.failure(Exception("User not found"))
                }
                datastore.saveAuthTokens(
                    accessToken = response.accessToken ?: "",
                    refreshToken = response.refreshToken ?: ""
                )
                Result.success(Unit)
            },
            onFailure = { throwable ->
                // If refresh token is invalid (401 or 403), we should log out the user
                val isAuthError = (throwable is io.ktor.client.plugins.ResponseException &&
                    (throwable.response.status.value == 401 || throwable.response.status.value == 403)) ||
                    (throwable.message?.contains("401") == true || throwable.message?.contains("403") == true)

                if (isAuthError) {
                    Log.e("AuthRepository", "Manual refresh failed with auth error. Logging out.", throwable)
                    datastore.clearAuthTokens()
                    datastore.setLoggedIn(false)
                }
                Result.failure(throwable)
            }
        )
    }

    override suspend fun logout(): Result<Unit> = runCatching {
        datastore.clearAuthTokens()
        userRepository.clearCurrentUser()
        datastore.setLoggedIn(false)
    }
}
