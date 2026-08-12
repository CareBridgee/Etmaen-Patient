package com.carenest.data.repository

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
import com.carenest.data.source.remote.service.ProfileApiService
import com.carenest.domain.usecase.profile.SyncAllergiesUseCase
import com.carenest.domain.usecase.profile.SyncMedicalConditionsUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ProfileCompletionIntegrationTest {
    @Test
    fun `medical conditions resolve catalog and custom values to name-only requests`() = runTest {
        val api = RecordingProfileApiService().apply {
            conditionCatalog = listOf(
                MedicalConditionDto(id = CONDITION_CATALOG_ID, name = "Diabetes")
            )
            medicalConditions = listOf(
                ProfileMedicalConditionResponseDto(
                    medicalConditionId = CONDITION_ID,
                    conditionName = "Asthma"
                )
            )
        }

        SyncMedicalConditionsUseCase(ProfileRepositoryImpl(api))(
            profileId = PROFILE_ID,
            selectedBackendIds = setOf(CONDITION_CATALOG_ID),
            otherConditions = " Rare condition "
        ).getOrThrow()

        assertEquals(setOf("Diabetes", "Rare condition"), api.conditionRequests.map { it.name }.toSet())
        api.conditionRequests.forEach {
            assertNull(it.medicalConditionId)
            assertNull(it.description)
        }
        assertEquals(listOf(CONDITION_ID), api.removedConditionIds)
    }

    @Test
    fun `allergies resolve catalog and custom values to name-only requests`() = runTest {
        val api = RecordingProfileApiService().apply {
            allergyCatalog = listOf(
                AllergyDto(id = ALLERGY_CATALOG_ID, name = "Penicillin", type = "DRUG")
            )
            allergies = listOf(
                ProfileAllergyResponseDto(
                    allergyId = ALLERGY_ID,
                    allergyName = "Peanuts",
                    allergyType = "FOOD"
                )
            )
        }

        SyncAllergiesUseCase(ProfileRepositoryImpl(api))(
            profileId = PROFILE_ID,
            selectedBackendIds = setOf(ALLERGY_CATALOG_ID),
            hasNoKnownAllergies = false,
            otherAllergies = " Latex "
        ).getOrThrow()

        assertEquals(setOf("Penicillin", "Latex"), api.allergyRequests.map { it.name }.toSet())
        api.allergyRequests.forEach {
            assertNull(it.allergyId)
            assertNull(it.type)
        }
        assertEquals(listOf(ALLERGY_ID), api.removedAllergyIds)
    }

    @Test
    fun `medications add by normalized name and remove by backend id`() = runTest {
        val api = RecordingProfileApiService().apply {
            medications = listOf(
                ProfileMedicationResponseDto(
                    id = MEDICATION_RELATION_ID,
                    profileId = PROFILE_ID,
                    medicationId = MEDICATION_ID,
                    medicationName = "Ibuprofen"
                )
            )
        }

        ProfileRepositoryImpl(api).syncProfileMedications(
            PROFILE_ID,
            listOf(" Aspirin ", "ASPIRIN")
        ).getOrThrow()

        assertEquals(1, api.medicationRequests.size)
        assertEquals("Aspirin", api.medicationRequests.single().name)
        assertNull(api.medicationRequests.single().medicationId)
        assertEquals(listOf(MEDICATION_ID), api.removedMedicationIds)
    }

    @Test
    fun `mobility update sends status and notes through profile update`() = runTest {
        val api = RecordingProfileApiService()

        ProfileRepositoryImpl(api).updateMobility(
            profileId = PROFILE_ID,
            mobilityStatus = "ASSISTED",
            mobilityNotes = "Uses a walker"
        ).getOrThrow()

        assertEquals("ASSISTED", api.profileRequest?.mobilityStatus)
        assertEquals("Uses a walker", api.profileRequest?.mobilityNotes)
        assertEquals(PROFILE_ID, api.updatedProfileId)
    }

    private companion object {
        const val PROFILE_ID = "11111111-1111-1111-1111-111111111111"
        const val CONDITION_ID = "22222222-2222-2222-2222-222222222222"
        const val ALLERGY_ID = "33333333-3333-3333-3333-333333333333"
        const val MEDICATION_RELATION_ID = "44444444-4444-4444-4444-444444444444"
        const val MEDICATION_ID = "55555555-5555-5555-5555-555555555555"
        const val CONDITION_CATALOG_ID = "66666666-6666-6666-6666-666666666666"
        const val ALLERGY_CATALOG_ID = "77777777-7777-7777-7777-777777777777"
    }
}

private class RecordingProfileApiService : ProfileApiService {
    var conditionCatalog = emptyList<MedicalConditionDto>()
    var allergyCatalog = emptyList<AllergyDto>()
    var medicalConditions = emptyList<ProfileMedicalConditionResponseDto>()
    var allergies = emptyList<ProfileAllergyResponseDto>()
    var medications = emptyList<ProfileMedicationResponseDto>()
    val conditionRequests = mutableListOf<ProfileMedicalConditionRequestDto>()
    val allergyRequests = mutableListOf<ProfileAllergyRequestDto>()
    val medicationRequests = mutableListOf<ProfileMedicationRequestDto>()
    val removedConditionIds = mutableListOf<String>()
    val removedAllergyIds = mutableListOf<String>()
    val removedMedicationIds = mutableListOf<String>()
    var profileRequest: ProfileRequestDto? = null
    var updatedProfileId: String? = null

    override suspend fun getProfileMedications(profileId: String) = Result.success(medications)

    override suspend fun addProfileMedication(
        profileId: String,
        request: ProfileMedicationRequestDto
    ): Result<ProfileMedicationResponseDto> {
        medicationRequests += request
        return Result.success(ProfileMedicationResponseDto())
    }

    override suspend fun removeProfileMedication(profileId: String, medicationId: String) =
        Result.success(Unit).also { removedMedicationIds += medicationId }

    override suspend fun updateProfile(
        profileId: String,
        request: ProfileRequestDto
    ): Result<ProfileResponseDto> {
        updatedProfileId = profileId
        profileRequest = request
        return Result.success(
            ProfileResponseDto(
                id = profileId,
                mobilityStatus = request.mobilityStatus,
                mobilityNotes = request.mobilityNotes
            )
        )
    }

    override suspend fun getProfileMedicalConditions(profileId: String) =
        Result.success(medicalConditions)

    override suspend fun addMedicalCondition(
        profileId: String,
        request: ProfileMedicalConditionRequestDto
    ): Result<ProfileMedicalConditionResponseDto> {
        conditionRequests += request
        return Result.success(ProfileMedicalConditionResponseDto())
    }

    override suspend fun removeMedicalCondition(
        profileId: String,
        medicalConditionId: String
    ) = Result.success(Unit).also { removedConditionIds += medicalConditionId }

    override suspend fun getProfileAllergies(profileId: String) = Result.success(allergies)

    override suspend fun addAllergy(
        profileId: String,
        request: ProfileAllergyRequestDto
    ): Result<ProfileAllergyResponseDto> {
        allergyRequests += request
        return Result.success(ProfileAllergyResponseDto())
    }

    override suspend fun removeAllergy(profileId: String, allergyId: String) =
        Result.success(Unit).also { removedAllergyIds += allergyId }

    override suspend fun getDefaultProfile() = unexpected<ProfileResponseDto>()
    override suspend fun getProfile(profileId: String) = unexpected<ProfileResponseDto>()
    override suspend fun getProfiles() = unexpected<List<ProfileResponseDto>>()
    override suspend fun createProfile(request: ProfileRequestDto) = unexpected<ProfileResponseDto>()
    override suspend fun getMedicalConditions() = Result.success(conditionCatalog)
    override suspend fun getAllergies() = Result.success(allergyCatalog)
    override suspend fun getEmergencyContacts(profileId: String) =
        unexpected<List<EmergencyContactResponseDto>>()
    override suspend fun getEmergencyContactById(emergencyContactId: String) =
        unexpected<EmergencyContactResponseDto>()
    override suspend fun createEmergencyContact(
        profileId: String,
        request: EmergencyContactRequestDto
    ) = unexpected<EmergencyContactResponseDto>()
    override suspend fun updateEmergencyContact(
        emergencyContactId: String,
        request: EmergencyContactRequestDto
    ) = unexpected<EmergencyContactResponseDto>()
    override suspend fun deleteEmergencyContact(emergencyContactId: String) = unexpected<Unit>()
    override suspend fun getProfileReport(profileId: String) = unexpected<com.carenest.data.source.remote.dto.profile.ProfileReportResponseDto>()

    private fun <T> unexpected(): Result<T> = Result.failure(AssertionError("Unexpected API call"))
}
