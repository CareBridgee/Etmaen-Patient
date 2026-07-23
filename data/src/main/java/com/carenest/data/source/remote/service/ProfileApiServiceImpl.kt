package com.carenest.data.source.remote.service

import com.carenest.data.source.remote.dto.ErrorResponse
import com.carenest.data.source.remote.dto.profile.*
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.http.path
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileApiServiceImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val json: Json
) : ProfileApiService {

    override suspend fun getDefaultProfile() = execute<ProfileResponseDto> {
        method = HttpMethod.Get
        url { path("api/v1/profiles/default") }
    }

    override suspend fun updateProfile(profileId: String, request: ProfileRequestDto) = execute<ProfileResponseDto> {
        method = HttpMethod.Put
        url { path("api/v1/profiles/$profileId") }
        contentType(ContentType.Application.Json)
        setBody(request)
    }

    override suspend fun getMedicalConditions() = execute<List<MedicalConditionDto>> {
        method = HttpMethod.Get
        url { path("api/v1/medical-conditions") }
    }

    override suspend fun getProfileMedicalConditions(profileId: String) = execute<List<ProfileMedicalConditionResponseDto>> {
        method = HttpMethod.Get
        url { path("api/v1/profiles/$profileId/medical-conditions") }
    }

    override suspend fun addMedicalCondition(profileId: String, request: ProfileMedicalConditionRequestDto) = execute<ProfileMedicalConditionResponseDto> {
        method = HttpMethod.Post
        url { path("api/v1/profiles/$profileId/medical-conditions") }
        contentType(ContentType.Application.Json)
        setBody(request)
    }

    override suspend fun removeMedicalCondition(profileId: String, medicalConditionId: String) = executeUnit {
        method = HttpMethod.Delete
        url { path("api/v1/profiles/$profileId/medical-conditions/$medicalConditionId") }
    }

    override suspend fun getAllergies() = execute<List<AllergyDto>> {
        method = HttpMethod.Get
        url { path("api/v1/allergies") }
    }

    override suspend fun getProfileAllergies(profileId: String) = execute<List<ProfileAllergyResponseDto>> {
        method = HttpMethod.Get
        url { path("api/v1/profiles/$profileId/allergies") }
    }

    override suspend fun addAllergy(profileId: String, request: ProfileAllergyRequestDto) = execute<ProfileAllergyResponseDto> {
        method = HttpMethod.Post
        url { path("api/v1/profiles/$profileId/allergies") }
        contentType(ContentType.Application.Json)
        setBody(request)
    }

    override suspend fun removeAllergy(profileId: String, allergyId: String) = executeUnit {
        method = HttpMethod.Delete
        url { path("api/v1/profiles/$profileId/allergies/$allergyId") }
    }

    override suspend fun getEmergencyContacts(profileId: String) = execute<List<EmergencyContactResponseDto>> {
        method = HttpMethod.Get
        url { path("api/v1/profiles/$profileId/emergency-contacts") }
    }

    override suspend fun createEmergencyContact(profileId: String, request: EmergencyContactRequestDto) = execute<EmergencyContactResponseDto> {
        method = HttpMethod.Post
        url { path("api/v1/profiles/$profileId/emergency-contacts") }
        contentType(ContentType.Application.Json)
        setBody(request)
    }

    override suspend fun updateEmergencyContact(emergencyContactId: String, request: EmergencyContactRequestDto) = execute<EmergencyContactResponseDto> {
        method = HttpMethod.Put
        url { path("api/v1/emergency-contacts/$emergencyContactId") }
        contentType(ContentType.Application.Json)
        setBody(request)
    }

    private suspend inline fun <reified T> execute(
        noinline block: HttpRequestBuilder.() -> Unit
    ): Result<T> = try {
        val response = httpClient.request(block)
        if (response.status.value !in 200..299) throw response.toProfileException()
        Result.success(response.body<T>())
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (error: Throwable) {
        Result.failure(error)
    }

    private suspend fun executeUnit(block: HttpRequestBuilder.() -> Unit): Result<Unit> = try {
        val response = httpClient.request(block)
        if (response.status.value !in 200..299) throw response.toProfileException()
        Result.success(Unit)
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (error: Throwable) {
        Result.failure(error)
    }

    private suspend fun io.ktor.client.statement.HttpResponse.toProfileException(): ProfileApiException {
        val raw = bodyAsText()
        val parsed = runCatching { json.decodeFromString<ErrorResponse>(raw) }.getOrNull()
        val message = parsed?.details?.values?.firstOrNull()
            ?: parsed?.message
            ?: "Request failed with HTTP ${status.value}"
        return ProfileApiException(status.value, parsed?.code, message)
    }
}
