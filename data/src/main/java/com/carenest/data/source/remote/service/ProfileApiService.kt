package com.carenest.data.source.remote.service

import com.carenest.data.source.remote.dto.profile.*

interface ProfileApiService {
    suspend fun getDefaultProfile(): Result<ProfileResponseDto>
    suspend fun getProfile(profileId: String): Result<ProfileResponseDto>
    suspend fun getProfiles(): Result<List<ProfileResponseDto>>
    suspend fun createProfile(request: ProfileRequestDto): Result<ProfileResponseDto>
    suspend fun updateProfile(profileId: String, request: ProfileRequestDto): Result<ProfileResponseDto>

    suspend fun getMedicalConditions(): Result<List<MedicalConditionDto>>
    suspend fun getProfileMedicalConditions(profileId: String): Result<List<ProfileMedicalConditionResponseDto>>
    suspend fun addMedicalCondition(profileId: String, request: ProfileMedicalConditionRequestDto): Result<ProfileMedicalConditionResponseDto>
    suspend fun removeMedicalCondition(profileId: String, medicalConditionId: String): Result<Unit>

    suspend fun getAllergies(): Result<List<AllergyDto>>
    suspend fun getProfileAllergies(profileId: String): Result<List<ProfileAllergyResponseDto>>
    suspend fun addAllergy(profileId: String, request: ProfileAllergyRequestDto): Result<ProfileAllergyResponseDto>
    suspend fun removeAllergy(profileId: String, allergyId: String): Result<Unit>

    suspend fun getEmergencyContacts(profileId: String): Result<List<EmergencyContactResponseDto>>
    suspend fun getEmergencyContactById(emergencyContactId: String): Result<EmergencyContactResponseDto>
    suspend fun createEmergencyContact(profileId: String, request: EmergencyContactRequestDto): Result<EmergencyContactResponseDto>
    suspend fun updateEmergencyContact(emergencyContactId: String, request: EmergencyContactRequestDto): Result<EmergencyContactResponseDto>
    suspend fun deleteEmergencyContact(emergencyContactId: String): Result<Unit>
}
