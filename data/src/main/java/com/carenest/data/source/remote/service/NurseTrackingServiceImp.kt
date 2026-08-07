package com.carenest.data.source.remote.service

import com.carenest.data.source.remote.dto.tracking.VisitCodeResponseDto
import com.carenest.data.utils.executeRequest
import com.carenest.domain.model.tracking.NurseTrackingInfo
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod
import io.ktor.http.path
import kotlinx.serialization.json.Json
import javax.inject.Inject

class NurseTrackingServiceImp @Inject constructor(
    private val httpClient: HttpClient, 
    private val json: Json
) : NurseTrackingService {
    override suspend fun fetchNurseTrackingInfo(requestId: String): NurseTrackingInfo {
        TODO("Not yet implemented")
    }

    override suspend fun cancelVisit(requestId: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun fetchVisitCode(requestId: String): Result<VisitCodeResponseDto> {
        return httpClient.executeRequest<VisitCodeResponseDto>(json) {
            method = HttpMethod.Post
            url {
                path("/api/v1/service-requests/$requestId/visit-code")
            }
        }
    }
}