package com.carenest.domain.repository

import com.carenest.domain.model.profile.Allergy
import com.carenest.domain.model.profile.BasicHealthUpdate
import com.carenest.domain.model.profile.EmergencyContact
import com.carenest.domain.model.profile.EmergencyContactInput
import com.carenest.domain.model.profile.MedicalCondition
import com.carenest.domain.model.profile.MedicalHistoryUpdate
import com.carenest.domain.model.profile.PersonalInfoUpdate
import com.carenest.domain.model.profile.Profile
import com.carenest.domain.model.profile.ProfileAllergy
import com.carenest.domain.model.profile.ProfileMedicalCondition
import com.carenest.domain.model.profile.ProfileMedication

interface ProfileRepository {
    suspend fun getProfileMedications(profileId: String): Result<List<ProfileMedication>>
    suspend fun syncProfileMedications(profileId: String, names: List<String>): Result<List<String>>
    suspend fun getDefaultProfile(): Result<Profile>
    suspend fun getProfile(profileId: String): Result<Profile>
    suspend fun getProfiles(): Result<List<Profile>>
    suspend fun createFamilyMember(
        relationship: String,
        firstName: String,
        lastName: String,
        dateOfBirth: String,
        gender: String
    ): Result<Profile>
    suspend fun updatePersonalInfo(profileId: String, update: PersonalInfoUpdate): Result<Profile>
    suspend fun updateBasicHealth(profileId: String, update: BasicHealthUpdate): Result<Profile>
    suspend fun updateMedicalHistory(profileId: String, update: MedicalHistoryUpdate): Result<Profile>
    suspend fun updateMobility(profileId: String, mobilityStatus: String, mobilityNotes: String): Result<Profile>

    suspend fun getMedicalConditionCatalog(): Result<List<MedicalCondition>>
    suspend fun getProfileMedicalConditions(profileId: String): Result<List<ProfileMedicalCondition>>
    suspend fun syncProfileMedicalConditions(
        profileId: String,
        originalBackendIds: Set<String>,
        selectedBackendIds: Set<String>
    ): Result<Set<String>>
    suspend fun syncProfileMedicalConditionsByName(
        profileId: String,
        names: List<String>
    ): Result<Unit>
    suspend fun addCustomMedicalCondition(
        profileId: String,
        name: String
    ): Result<ProfileMedicalCondition>

    suspend fun getAllergyCatalog(): Result<List<Allergy>>
    suspend fun getProfileAllergies(profileId: String): Result<List<ProfileAllergy>>
    suspend fun syncProfileAllergies(
        profileId: String,
        originalBackendIds: Set<String>,
        selectedBackendIds: Set<String>
    ): Result<Set<String>>
    suspend fun syncProfileAllergiesByName(
        profileId: String,
        names: List<String>
    ): Result<Unit>
    suspend fun addCustomAllergy(profileId: String, name: String): Result<ProfileAllergy>

    suspend fun getEmergencyContacts(profileId: String): Result<List<EmergencyContact>>
    suspend fun getEmergencyContactById(emergencyContactId: String): Result<EmergencyContact>
    suspend fun createEmergencyContact(
        profileId: String,
        input: EmergencyContactInput
    ): Result<EmergencyContact>
    suspend fun updateEmergencyContact(
        emergencyContactId: String,
        input: EmergencyContactInput
    ): Result<EmergencyContact>
    suspend fun deleteEmergencyContact(emergencyContactId: String): Result<Unit>
    suspend fun getProfileReport(profileId: String): Result<String>
}
