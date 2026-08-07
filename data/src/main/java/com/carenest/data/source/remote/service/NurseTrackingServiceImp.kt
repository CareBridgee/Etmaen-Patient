package com.carenest.data.source.remote.service

import com.carenest.data.source.remote.dto.tracking.CancelRequest
import com.carenest.data.source.remote.dto.tracking.NurseOfferDto
import com.carenest.data.source.remote.dto.tracking.VisitCodeResponseDto
import com.carenest.data.utils.executeRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpMethod
import io.ktor.http.isSuccess
import io.ktor.http.path
import kotlinx.serialization.json.Json
import javax.inject.Inject

class NurseTrackingServiceImp @Inject constructor(
    private val httpClient: HttpClient, private val json: Json
) : NurseTrackingService {

    override suspend fun cancelVisit(requestId: String): Boolean {
        val response = httpClient.post("/api/v1/service-requests/$requestId/cancel") {
            setBody(
                CancelRequest(
                    reason = "USER_CANCELLED",
                    note = "Cancelled by user from tracking screen"
                )
            )
        }
        return response.status.isSuccess()
    }

    override suspend fun fetchVisitCode(requestId: String): Result<VisitCodeResponseDto> {
        return httpClient.executeRequest<VisitCodeResponseDto>(json) {
            method = HttpMethod.Post
            url {
                path("/api/v1/service-requests/$requestId/visit-code")
            }
        }
    }

    override suspend fun fetchNurseOffer(offerId: String): Result<NurseOfferDto> {
        return httpClient.executeRequest<NurseOfferDto>(json) {
            method = HttpMethod.Get
            url {
                path("/api/v1/nurse-offers/$offerId")
            }
        }
    }
}