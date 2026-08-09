package com.carenest.data.source.remote.service

import com.carenest.data.source.remote.dto.profile.ProfileRequestDto
import com.carenest.data.source.remote.dto.profile.ProfileResponseDto
import com.carenest.data.source.remote.dto.profile.toMultipartFormData
import com.carenest.data.utils.executeRequest
import com.carenest.data.utils.executeUnitRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.setBody
import io.ktor.http.HttpMethod
import io.ktor.http.path
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FamilyMembersApiServiceImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val json: Json
) : FamilyMembersApiService {

    override suspend fun getFamilyMembers(): Result<List<ProfileResponseDto>> =
        httpClient.executeRequest<List<ProfileResponseDto>>(json) {
            method = HttpMethod.Get
            url { path("api/v1/profiles") }
        }

    override suspend fun getFamilyMemberById(id: String): Result<ProfileResponseDto> =
        httpClient.executeRequest<ProfileResponseDto>(json) {
            method = HttpMethod.Get
            url { path("api/v1/profiles/$id") }
        }

    override suspend fun createFamilyMember(request: ProfileRequestDto): Result<ProfileResponseDto> =
        httpClient.executeRequest<ProfileResponseDto>(json) {
            method = HttpMethod.Post
            url { path("api/v1/profiles") }
            setBody(request.toMultipartFormData())
        }

    override suspend fun updateFamilyMember(id: String, request: ProfileRequestDto): Result<ProfileResponseDto> =
        httpClient.executeRequest<ProfileResponseDto>(json) {
            method = HttpMethod.Put
            url { path("api/v1/profiles/$id") }
            setBody(request.toMultipartFormData())
        }

    override suspend fun deleteFamilyMember(id: String): Result<Unit> =
        httpClient.executeUnitRequest(json) {
            method = HttpMethod.Delete
            url { path("api/v1/profiles/$id") }
        }
}
