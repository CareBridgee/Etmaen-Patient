package com.carenest.data.source.remote.service

import com.carenest.data.source.remote.dto.family_members.FamilyMemberRequestDto
import com.carenest.data.source.remote.dto.family_members.FamilyMemberResponseDto
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
import javax.inject.Singleton

@Singleton
class FamilyMembersApiServiceImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val json: Json
) : FamilyMembersApiService {

    override suspend fun getFamilyMembers(profileId: String?): Result<List<FamilyMemberResponseDto>> {
        val pathStr = if (!profileId.isNullOrBlank()) {
            "api/v1/profiles/$profileId/emergency-contacts"
        } else {
            "api/v1/profiles"
        }
        return httpClient.executeRequest<List<FamilyMemberResponseDto>>(json) {
            method = HttpMethod.Get
            url { path(pathStr) }
        }
    }

    override suspend fun getFamilyMemberById(id: String) = httpClient.executeRequest<FamilyMemberResponseDto>(json) {
        method = HttpMethod.Get
        url { path("api/v1/emergency-contacts/$id") }
    }

    override suspend fun createFamilyMember(profileId: String?, request: FamilyMemberRequestDto): Result<FamilyMemberResponseDto> {
        val pathStr = if (!profileId.isNullOrBlank()) {
            "api/v1/profiles/$profileId/emergency-contacts"
        } else {
            "api/v1/profiles"
        }
        return httpClient.executeRequest<FamilyMemberResponseDto>(json) {
            method = HttpMethod.Post
            url { path(pathStr) }
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    override suspend fun updateFamilyMember(id: String, request: FamilyMemberRequestDto) = httpClient.executeRequest<FamilyMemberResponseDto>(json) {
        method = HttpMethod.Put
        url { path("api/v1/emergency-contacts/$id") }
        contentType(ContentType.Application.Json)
        setBody(request)
    }

    override suspend fun deleteFamilyMember(id: String) = httpClient.executeUnitRequest(json) {
        method = HttpMethod.Delete
        url { path("api/v1/emergency-contacts/$id") }
    }
}
