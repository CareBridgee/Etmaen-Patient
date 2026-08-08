package com.carenest.data.source.remote.service

import com.carenest.data.source.remote.dto.user.UpdateUserRequestDto
import com.carenest.data.source.remote.dto.user.UserResponseDto
import com.carenest.data.utils.executeRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.http.path
import kotlinx.serialization.json.Json
import javax.inject.Inject

import com.carenest.data.source.remote.dto.user.FileUploadRequestDto

class UserApiServiceImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val json: Json
) : UserApiService {
    override suspend fun getCurrentUser(): Result<UserResponseDto> =
        httpClient.executeRequest(json) {
            method = HttpMethod.Get
            url { path("api/v1/users/me") }
        }

    override suspend fun uploadProfileImage(
        fileName: String,
        contentType: String,
        bytes: ByteArray
    ): Result<String> = httpClient.executeRequest<Map<String, String>>(json) {
        method = HttpMethod.Post
        url { path("api/v1/upload") }
        contentType(ContentType.Application.Json)
        val base64String = android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)
        setBody(FileUploadRequestDto(file = base64String))
    }.mapCatching { response ->
        response["url"]
            ?: response["fileUrl"]
            ?: response["path"]
            ?: response.values.firstOrNull(String::isNotBlank)
            ?: error("Upload response did not contain an image URL")
    }

    override suspend fun updateCurrentUser(
        request: UpdateUserRequestDto
    ): Result<UserResponseDto> = httpClient.executeRequest(json) {
        method = HttpMethod.Put
        url { path("api/v1/users/me") }
        contentType(ContentType.Application.Json)
        setBody(request)
    }
}
