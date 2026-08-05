package com.carenest.data.source.remote.service

import com.carenest.data.source.remote.dto.ServiceDto
import com.carenest.data.utils.executeRequest
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod
import io.ktor.http.path
import kotlinx.serialization.json.Json
import javax.inject.Inject


class CareNestApiServiceImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val json: Json
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
}
