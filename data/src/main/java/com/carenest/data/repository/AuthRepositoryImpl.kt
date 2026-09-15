package com.carenest.data.repository

import android.util.Log
import com.carenest.data.di.qualifier.IoDispatcher
import com.carenest.data.source.local.preferences.CarenestDatastore
import com.carenest.data.source.remote.datasource.auth.AuthDatasource
import com.carenest.domain.model.auth.AuthResult
import com.carenest.domain.model.auth.GoogleAuthResult
import com.carenest.domain.model.auth.AuthException
import com.carenest.domain.model.auth.AuthFailure
import com.carenest.domain.repository.AuthRepository
import com.carenest.domain.repository.UserRepository
import com.carenest.data.source.remote.ApiException
import java.io.IOException
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
            .mapAuthFailure(AuthOperation.REQUEST_OTP)

    override suspend fun loginWithGoogle(idToken: String): Result<GoogleAuthResult> {
        return authDatasource.loginWithGoogle(idToken).fold(
            onSuccess = { response ->
                runCatching {
                    when (response.status) {
                        "AUTHENTICATED" -> {
                            val accessToken = response.accessToken ?: throw Exception("Missing access token")
                            val refreshToken = response.refreshToken ?: throw Exception("Missing refresh token")
                            val expiresIn = response.expiresIn ?: throw Exception("Missing expiresIn")

                            datastore.saveAuthTokens(
                                accessToken = accessToken,
                                refreshToken = refreshToken,
                            )

                            val user = userRepository.refreshCurrentUser().getOrThrow()
                            datastore.setUserId(user.id)
                            datastore.setLoggedIn(true)

                            GoogleAuthResult.Authenticated(
                                AuthResult(
                                    accessToken = accessToken,
                                    refreshToken = refreshToken,
                                    expiresIn = expiresIn,
                                    user = user
                                )
                            )
                        }
                        "PHONE_REQUIRED" -> {
                            val pendingToken = response.pendingToken ?: throw Exception("Missing pendingToken")
                            val email = response.email ?: throw Exception("Missing email")
                            
                            GoogleAuthResult.PhoneRequired(
                                pendingToken = pendingToken,
                                email = email,
                                firstName = response.firstName,
                                lastName = response.lastName,
                                profileImageUrl = response.profileImageUrl
                            )
                        }
                        else -> throw Exception("Unknown status: ${response.status}")
                    }
                }.onFailure {
                    clearUserSession()
                }
            },
            onFailure = { Result.failure(it.toAuthException(AuthOperation.LOGIN_GOOGLE)) }
        )
    }

    override suspend fun requestDevOtp(phoneNumber: String): Result<String?> {
        return authDatasource.requestDevOtp(phoneNumber)
            .mapAuthFailure(AuthOperation.REQUEST_OTP)
            .map { it.otp }
    }

    override suspend fun verifyOtp(phoneNumber: String, otp: String, pendingToken: String?): Result<AuthResult> {
        return authDatasource.verifyOtp(phoneNumber, otp, pendingToken).fold(
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
                    clearUserSession()
                }
            },
            onFailure = { Result.failure(it.toAuthException(AuthOperation.VERIFY_OTP)) }
        )
    }

    override suspend fun refreshToken(): Result<Unit> = withContext(dispatcher) {
        val tokens = datastore.authTokens.first()
        val refreshToken = tokens?.refreshToken ?: return@withContext Result.failure(Exception("No refresh token"))

        authDatasource.refreshToken(refreshToken).fold(
            onSuccess = { response ->
                if (response.refreshToken?.isBlank() == true || response.accessToken?.isBlank() == true) {
                    clearUserSession()
                    return@withContext Result.failure(Exception("User not found"))
                }
                datastore.saveAuthTokens(
                    accessToken = response.accessToken ?: "",
                    refreshToken = response.refreshToken ?: ""
                )
                Result.success(Unit)
            },
            onFailure = { throwable ->
                val isAuthError = (throwable is io.ktor.client.plugins.ResponseException &&
                    (throwable.response.status.value == 401 || throwable.response.status.value == 403)) ||
                    (throwable.message?.contains("401") == true || throwable.message?.contains("403") == true)

                if (isAuthError) {
                    Log.e("AuthRepository", "Manual refresh failed with auth error. Logging out.", throwable)
                    clearUserSession()
                }
                Result.failure(throwable)
            }
        )
    }

    override suspend fun logout(): Result<Unit> = runCatching {
        clearUserSession()
    }

    private suspend fun clearUserSession() {
        datastore.clearAuthTokens()
        datastore.clearUserId()
        datastore.setLoggedIn(false)
        userRepository.clearCurrentUser()
    }
}

private enum class AuthOperation {
    REQUEST_OTP,
    VERIFY_OTP,
    LOGIN_GOOGLE
}

private fun <T> Result<T>.mapAuthFailure(operation: AuthOperation): Result<T> =
    fold(
        onSuccess = Result.Companion::success,
        onFailure = { Result.failure(it.toAuthException(operation)) }
    )

private fun Throwable.toAuthException(operation: AuthOperation): AuthException {
    if (this is AuthException) return this

    val apiError = this as? ApiException
    val statusCode = apiError?.statusCode
    val backendCode = apiError?.backendCode
    val searchableMessage = listOfNotNull(backendCode, message)
        .joinToString(" ")
        .lowercase()

    val failure = when {
        this is IOException ||
            searchableMessage.contains("timeout") ||
            searchableMessage.contains("unable to resolve host") ||
            searchableMessage.contains("failed to connect") -> AuthFailure.Network
        searchableMessage.contains("too many") ||
            searchableMessage.contains("rate limit") -> AuthFailure.TooManyRequests
        operation == AuthOperation.VERIFY_OTP && searchableMessage.contains("expired") ->
            AuthFailure.ExpiredOtp
        operation == AuthOperation.VERIFY_OTP &&
            (searchableMessage.contains("invalid otp") ||
                searchableMessage.contains("incorrect code") ||
                searchableMessage.contains("invalid code")) -> AuthFailure.InvalidOtp
        statusCode == 408 -> AuthFailure.Network
        statusCode == 429 -> AuthFailure.TooManyRequests
        statusCode == 409 -> AuthFailure.PhoneAlreadyTaken
        statusCode != null && statusCode >= 500 -> AuthFailure.Server
        operation == AuthOperation.VERIFY_OTP && statusCode in setOf(400, 401, 403, 404, 422) ->
            AuthFailure.InvalidOtp
        operation == AuthOperation.REQUEST_OTP && statusCode in setOf(400, 404, 422) ->
            AuthFailure.InvalidPhone
        else -> AuthFailure.Unknown
    }

    return AuthException(
        failure = failure,
        message = message ?: "Authentication request failed",
        statusCode = statusCode,
        backendCode = backendCode,
        details = apiError?.details,
        cause = this
    )
}
