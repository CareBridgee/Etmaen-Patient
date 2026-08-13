package com.carenest.data.source.remote.service

import com.carenest.data.source.remote.dto.profile.AllergyDto
import com.carenest.data.source.remote.dto.profile.EmergencyContactRequestDto
import com.carenest.data.source.remote.dto.profile.EmergencyContactResponseDto
import com.carenest.data.source.remote.dto.profile.MedicalConditionDto
import com.carenest.data.source.remote.dto.profile.ProfileAllergyRequestDto
import com.carenest.data.source.remote.dto.profile.ProfileAllergyResponseDto
import com.carenest.data.source.remote.dto.profile.ProfileMedicalConditionRequestDto
import com.carenest.data.source.remote.dto.profile.ProfileMedicalConditionResponseDto
import com.carenest.data.source.remote.dto.profile.ProfileMedicationRequestDto
import com.carenest.data.source.remote.dto.profile.ProfileMedicationResponseDto
import com.carenest.data.source.remote.dto.profile.ProfileRequestDto
import com.carenest.data.source.remote.dto.profile.ProfileResponseDto
import com.carenest.data.source.remote.dto.profile.toMultipartFormData
import com.carenest.data.utils.executeRequest
import com.carenest.data.utils.executeUnitRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.http.path
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileApiServiceImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val json: Json
) : ProfileApiService {

    override suspend fun getProfileMedications(profileId: String) =
        httpClient.executeRequest<List<ProfileMedicationResponseDto>>(json) {
            method = HttpMethod.Get
            url { path("api/v1/profiles/$profileId/medications") }
        }

    override suspend fun addProfileMedication(
        profileId: String,
        request: ProfileMedicationRequestDto
    ) = httpClient.executeRequest<ProfileMedicationResponseDto>(json) {
        method = HttpMethod.Post
        url { path("api/v1/profiles/$profileId/medications") }
        contentType(ContentType.Application.Json)
        setBody(request)
    }

    override suspend fun removeProfileMedication(profileId: String, medicationId: String) =
        httpClient.executeUnitRequest(json) {
            method = HttpMethod.Delete
            url { path("api/v1/profiles/$profileId/medications/$medicationId") }
        }

    override suspend fun getDefaultProfile() = httpClient.executeRequest<ProfileResponseDto>(json) {
        method = HttpMethod.Get
        url { path("api/v1/profiles/default") }
    }

    override suspend fun getProfile(profileId: String) =
        httpClient.executeRequest<ProfileResponseDto>(json) {
            method = HttpMethod.Get
            url { path("api/v1/profiles/$profileId") }
        }
    override suspend fun getProfiles() = httpClient.executeRequest<List<ProfileResponseDto>>(json) {
        method = HttpMethod.Get
        url { path("api/v1/profiles") }
    }



    override suspend fun createProfile(request: ProfileRequestDto) = httpClient.executeRequest<ProfileResponseDto>(json) {
        method = HttpMethod.Post
        url { path("api/v1/profiles") }
        setBody(request.toMultipartFormData())
    }

    override suspend fun updateProfile(profileId: String, request: ProfileRequestDto) =
        httpClient.executeRequest<ProfileResponseDto>(json) {
            method = HttpMethod.Put
            url { path("api/v1/profiles/$profileId") }

            setBody(request.toMultipartFormData())
        }

    override suspend fun getMedicalConditions() =
        httpClient.executeRequest<List<MedicalConditionDto>>(json) {
            method = HttpMethod.Get
            url { path("api/v1/medical-conditions") }
        }

    override suspend fun getProfileMedicalConditions(profileId: String) =
        httpClient.executeRequest<List<ProfileMedicalConditionResponseDto>>(json) {
            method = HttpMethod.Get
            url { path("api/v1/profiles/$profileId/medical-conditions") }
        }

    override suspend fun addMedicalCondition(
        profileId: String,
        request: ProfileMedicalConditionRequestDto
    ) = httpClient.executeRequest<ProfileMedicalConditionResponseDto>(json) {
        method = HttpMethod.Post
        url { path("api/v1/profiles/$profileId/medical-conditions") }
        contentType(ContentType.Application.Json)
        setBody(request)
    }

    override suspend fun removeMedicalCondition(
        profileId: String,
        medicalConditionId: String
    ) = httpClient.executeUnitRequest(json) {
        method = HttpMethod.Delete
        url { path("api/v1/profiles/$profileId/medical-conditions/$medicalConditionId") }
    }

    override suspend fun getAllergies() = httpClient.executeRequest<List<AllergyDto>>(json) {
        method = HttpMethod.Get
        url { path("api/v1/allergies") }
    }

    override suspend fun getProfileAllergies(profileId: String) =
        httpClient.executeRequest<List<ProfileAllergyResponseDto>>(json) {
            method = HttpMethod.Get
            url { path("api/v1/profiles/$profileId/allergies") }
        }

    override suspend fun addAllergy(profileId: String, request: ProfileAllergyRequestDto) =
        httpClient.executeRequest<ProfileAllergyResponseDto>(json) {
            method = HttpMethod.Post
            url { path("api/v1/profiles/$profileId/allergies") }
            contentType(ContentType.Application.Json)
            setBody(request)
        }

    override suspend fun removeAllergy(profileId: String, allergyId: String) =
        httpClient.executeUnitRequest(json) {
            method = HttpMethod.Delete
            url { path("api/v1/profiles/$profileId/allergies/$allergyId") }
        }

    override suspend fun getEmergencyContacts(profileId: String) =
        httpClient.executeRequest<List<EmergencyContactResponseDto>>(json) {
            method = HttpMethod.Get
            url { path("api/v1/profiles/$profileId/emergency-contacts") }
        }

    override suspend fun getEmergencyContactById(emergencyContactId: String) =
        httpClient.executeRequest<EmergencyContactResponseDto>(json) {
            method = HttpMethod.Get
            url { path("api/v1/emergency-contacts/$emergencyContactId") }
        }

    override suspend fun createEmergencyContact(
        profileId: String,
        request: EmergencyContactRequestDto
    ) = httpClient.executeRequest<EmergencyContactResponseDto>(json) {
        method = HttpMethod.Post
        url { path("api/v1/profiles/$profileId/emergency-contacts") }
        contentType(ContentType.Application.Json)
        setBody(request)
    }

    override suspend fun updateEmergencyContact(
        emergencyContactId: String,
        request: EmergencyContactRequestDto
    ) = httpClient.executeRequest<EmergencyContactResponseDto>(json) {
        method = HttpMethod.Put
        url { path("api/v1/emergency-contacts/$emergencyContactId") }
        contentType(ContentType.Application.Json)
        setBody(request)
    }

    override suspend fun deleteEmergencyContact(emergencyContactId: String) =
        httpClient.executeUnitRequest(json) {
            method = HttpMethod.Delete
            url { path("api/v1/emergency-contacts/$emergencyContactId") }
        }
}
