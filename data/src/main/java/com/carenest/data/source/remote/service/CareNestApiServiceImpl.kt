package com.carenest.data.source.remote.service

import com.carenest.data.source.remote.dto.ServiceDto
import com.carenest.data.source.remote.dto.history.ReviewRequestDto
import com.carenest.data.source.remote.dto.CreateServiceRequestDto
import com.carenest.data.source.remote.dto.ServiceRequestResponseDto
import com.carenest.data.source.remote.dto.history.ServiceHistoryDto
import com.carenest.data.source.remote.dto.history.VisitSummaryResponseDto
import com.carenest.data.utils.executeRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.http.path
import kotlinx.serialization.json.Json
import javax.inject.Inject


class CareNestApiServiceImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val json: Json,
): CareNestApiService {
    override suspend fun getServices(): Result<List<ServiceDto>> {
        return httpClient.executeRequest<List<ServiceDto>>(json) {
            method = HttpMethod.Get
            url {
                path("api/v1/service-types")
            }
        }
    }

    override suspend fun getServiceDetails(serviceId: String): Result<ServiceDto> {
        return httpClient.executeRequest<ServiceDto>(json) {
            method = HttpMethod.Get
            url {
                path("/api/v1/service-types/${serviceId}")
            }
        }
    }

    override suspend fun getServiceHistory(): Result<List<ServiceHistoryDto>> {
        return httpClient.executeRequest<List<ServiceHistoryDto>>(json) {
            method = HttpMethod.Get
            url {
                path("api/v1/service-requests/confirmed")
            }
        }
    }

    override suspend fun getServiceRequestDetails(requestId: String): Result<VisitSummaryResponseDto> {
        return httpClient.executeRequest<VisitSummaryResponseDto>(json) {
            method = HttpMethod.Get
            url {
                path("api/v1/service-requests/$requestId")
            }
        }
    }

    override suspend fun submitReview(review: ReviewRequestDto): Result<Unit> {
        return httpClient.executeRequest<Unit>(json) {
            method = HttpMethod.Post
            url {
                path("api/v1/reviews")
            }
            contentType(ContentType.Application.Json)
            setBody(review)
        }
    }

    override suspend fun submitServiceRequest(body: CreateServiceRequestDto): Result<ServiceRequestResponseDto> {
        return httpClient.executeRequest<ServiceRequestResponseDto>(json) {
            method = HttpMethod.Post
            url { path("api/v1/service-requests") }
            contentType(ContentType.Application.Json)
            setBody(body)
        }
    }
}