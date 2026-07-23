package com.carenest.domain.repository

import com.carenest.domain.model.profile.*

interface ProfileRepository {
    suspend fun getDefaultProfile(): Result<Profile>
    suspend fun updatePersonalInfo(profileId: String, update: PersonalInfoUpdate): Result<Profile>
    suspend fun updateBasicHealth(profileId: String, update: BasicHealthUpdate): Result<Profile>
    suspend fun updateMedicalHistory(profileId: String, update: MedicalHistoryUpdate): Result<Profile>
    suspend fun updateMobility(profileId: String, update: MobilityUpdate): Result<Profile>

    suspend fun getMedicalConditionCatalog(): Result<List<MedicalCondition>>
    suspend fun getProfileMedicalConditions(profileId: String): Result<List<ProfileMedicalCondition>>
    suspend fun syncProfileMedicalConditions(
        profileId: String,
        originalBackendIds: Set<String>,
        selectedLocalKeys: Set<String>
    ): Result<Set<String>>

    suspend fun getAllergyCatalog(): Result<List<Allergy>>
    suspend fun getProfileAllergies(profileId: String): Result<List<ProfileAllergy>>
    suspend fun syncProfileAllergies(
        profileId: String,
        originalBackendIds: Set<String>,
        selectedLocalKeys: Set<String>
    ): Result<Set<String>>

    suspend fun getMedicationCatalog(): Result<List<Medication>>
    suspend fun getProfileMedications(profileId: String): Result<List<ProfileMedication>>
    suspend fun syncProfileMedications(
        profileId: String,
        originalBackendIds: Set<String>,
        entries: List<LocalMedicationEntry>
    ): Result<List<LocalMedicationEntry>>

    suspend fun getEmergencyContacts(profileId: String): Result<List<EmergencyContact>>
    suspend fun createEmergencyContact(profileId: String, input: EmergencyContactInput): Result<EmergencyContact>
    suspend fun updateEmergencyContact(emergencyContactId: String, input: EmergencyContactInput): Result<EmergencyContact>

    suspend fun loadLocalDraft(userId: String): Result<ProfileLocalDraft>
    suspend fun saveLocalDraft(userId: String, draft: ProfileLocalDraft): Result<Unit>
    suspend fun markHealthProfileOnboardingHandled(userId: String): Result<Unit>
}
