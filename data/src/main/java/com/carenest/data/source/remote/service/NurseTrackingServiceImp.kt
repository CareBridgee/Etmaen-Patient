package com.carenest.data.source.remote.service

import com.carenest.data.socket.models.ChatMessageResponseDto
import com.carenest.data.socket.models.SendMessageRequestDto
import com.carenest.data.source.remote.dto.tracking.NurseDetailsDto
import com.carenest.data.source.remote.dto.tracking.ServiceRequestTrackingDto
import com.carenest.data.source.remote.dto.tracking.VisitCodeResponseDto
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

class NurseTrackingServiceImp @Inject constructor(
    private val httpClient: HttpClient, private val json: Json
) : NurseTrackingService {

    override suspend fun cancelVisit(requestId: String): Boolean {
        val result = httpClient.executeUnitRequest(json) {
            method = HttpMethod.Patch
            url.path("/api/v1/service-requests/$requestId/cancel")
        }
        return result.isSuccess
    }

    override suspend fun fetchVisitCode(requestId: String): Result<VisitCodeResponseDto> {
        return httpClient.executeRequest<VisitCodeResponseDto>(json) {
            method = HttpMethod.Post
            url {
                path("/api/v1/service-requests/$requestId/visit-code")
            }
        }
    }

    override suspend fun fetchServiceRequest(requestId: String): Result<ServiceRequestTrackingDto> {
        return httpClient.executeRequest<ServiceRequestTrackingDto>(json) {
            method = HttpMethod.Get
            url {
                path("/api/v1/service-requests/$requestId")
            }
        }
    }

    override suspend fun fetchCurrentServiceRequest(): Result<ServiceRequestTrackingDto> {
        return httpClient.executeRequest<ServiceRequestTrackingDto>(json) {
            method = HttpMethod.Get
            url {
                path("/api/v1/service-requests/current")
            }
        }
    }

    override suspend fun fetchNurseDetails(nurseId: String): Result<NurseDetailsDto> {
        return httpClient.executeRequest<NurseDetailsDto>(json) {
            method = HttpMethod.Get
            url {
                path("/api/v1/nurses/$nurseId")
            }
        }
    }


    override suspend fun getChatMessages(reservationId: String): Result<List<ChatMessageResponseDto>> {
        return httpClient.executeRequest<List<ChatMessageResponseDto>>(json) {
            method = HttpMethod.Get
            url {
                path("api/v1/reservations/$reservationId/messages")
            }
        }
    }

    override suspend fun sendChatMessage(reservationId: String, body: SendMessageRequestDto): Result<ChatMessageResponseDto> {
        return httpClient.executeRequest<ChatMessageResponseDto>(json) {
            method = HttpMethod.Post
            url {
                path("api/v1/reservations/$reservationId/messages")
            }
            contentType(ContentType.Application.Json)
            setBody(body)
        }
    }
}