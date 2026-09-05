package com.carenest.data.source.remote.service

import com.carenest.data.di.qualifier.AuthHttpClient
import com.carenest.data.source.remote.dto.AuthResponse
import com.carenest.data.source.remote.dto.GoogleAuthResponseDto
import com.carenest.data.source.remote.dto.GoogleLoginRequest
import com.carenest.data.source.remote.dto.LoginRequest
import com.carenest.data.source.remote.dto.LoginResponse
import com.carenest.data.source.remote.dto.RefreshRequest
import com.carenest.data.source.remote.dto.TokenPairResponse
import com.carenest.data.source.remote.dto.VerifyOtpRequest
import com.carenest.data.utils.executeRequest
import com.carenest.data.utils.executeUnitRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.http.path
import kotlinx.serialization.json.Json
import javax.inject.Inject

class AuthApiServiceImpl @Inject constructor(
    @param:AuthHttpClient private val httpClient: HttpClient,
    private val json: Json
) : AuthApiService {

    override suspend fun loginWithPhone(phoneNumber: String): Result<Unit> =
        httpClient.executeUnitRequest(json) {
            method = HttpMethod.Post
            url { path("api/v1/auth/login") }
            setBody(LoginRequest(phoneNumber))
            contentType(ContentType.Application.Json)
        }

    override suspend fun loginWithGoogle(idToken: String): Result<GoogleAuthResponseDto> =
        httpClient.executeRequest<GoogleAuthResponseDto>(json) {
            method = HttpMethod.Post
            url { path("api/v1/auth/google") }
            setBody(GoogleLoginRequest(idToken))
            contentType(ContentType.Application.Json)
        }

    override suspend fun requestDevOtp(phoneNumber: String): Result<LoginResponse> =
        httpClient.executeRequest<LoginResponse>(json) {
            method = HttpMethod.Post
            url { path("api/v1/auth/dev/request-otp") }
            setBody(LoginRequest(phoneNumber))
            contentType(ContentType.Application.Json)
        }

    override suspend fun verifyOtp(phoneNumber: String, otp: String, pendingToken: String?): Result<AuthResponse> =
        httpClient.executeRequest<AuthResponse>(json) {
            method = HttpMethod.Post
            url { path("api/v1/auth/verify-otp") }
            setBody(VerifyOtpRequest(phoneNumber, otp, pendingToken))
            contentType(ContentType.Application.Json)
        }

    override suspend fun refreshToken(refreshToken: String): Result<TokenPairResponse> =
        httpClient.executeRequest<TokenPairResponse>(json) {
            method = HttpMethod.Post
            url { path("api/v1/auth/refresh") }
            setBody(RefreshRequest(refreshToken))
            contentType(ContentType.Application.Json)
        }
}
