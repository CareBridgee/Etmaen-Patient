package com.carenest.data.source.remote.service

import com.carenest.data.source.remote.dto.aichat.AiChatRequestDto
import com.carenest.data.source.remote.dto.aichat.AiChatResponseDto
import com.carenest.data.utils.executeRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.http.path
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiChatApiServiceImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val json: Json
) : AiChatApiService {

    override suspend fun sendChatMessage(request: AiChatRequestDto): Result<AiChatResponseDto> =
        httpClient.executeRequest<AiChatResponseDto>(json) {
            method = HttpMethod.Post
            url { path("api/v1/chat") }
            contentType(ContentType.Application.Json)
            setBody(request)
        }
}
