package com.carenest.data.source.remote.service

import com.carenest.data.source.local.preferences.CarenestDatastore
import com.carenest.data.source.remote.dto.ServiceDto
import com.carenest.data.source.remote.dto.UserDto
import com.carenest.data.utils.executeRequest
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod
import io.ktor.http.path
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import javax.inject.Inject


class CareNestApiServiceImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val json: Json,
    private val datastore: CarenestDatastore
): CareNestApiService {
    override suspend fun getServices(): Result<List<ServiceDto>> {
        return httpClient.executeRequest<List<ServiceDto>>(json) {
            method = HttpMethod.Get
            url {
                path("api/v1/service-types")
            }
        }
    }

    override suspend fun getUser(): Result<UserDto> {
        val userId = datastore.userId.first()
        return httpClient.executeRequest<UserDto>(json) {
            method = HttpMethod.Get
            url {
                path("/api/v1/users/${userId}")
            }
        }
    }
}