package com.carenest.data.source.remote.service

import com.carenest.data.source.remote.dto.user.UpdateUserRequestDto
import com.carenest.data.source.remote.dto.user.UserResponseDto
import com.carenest.data.utils.executeRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.setBody
import io.ktor.http.HttpMethod
import io.ktor.http.path
import kotlinx.serialization.json.Json
import javax.inject.Inject

class UserApiServiceImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val json: Json
) : UserApiService {
    override suspend fun getCurrentUser(): Result<UserResponseDto> =
        httpClient.executeRequest(json) {
            method = HttpMethod.Get
            url { path("api/v1/users/me") }
        }

    override suspend fun updateCurrentUser(
        request: UpdateUserRequestDto
    ): Result<UserResponseDto> = httpClient.executeRequest(json) {
        method = HttpMethod.Put
        url { path("api/v1/users/me") }
        setBody(MultiPartFormDataContent(formData {
            append("firstName", request.firstName)
            append("lastName", request.lastName)
            request.email?.let { append("email", it) }
            request.dateOfBirth?.let { append("dateOfBirth", it) }
            request.gender?.let { append("gender", it) }
            request.profileImageUrl?.let { append("profileImageUrl", it) }
        }))
    }
}

