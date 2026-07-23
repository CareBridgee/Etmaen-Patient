package com.carenest.data.repository

import com.carenest.data.mapper.profile.toDomain
import com.carenest.data.source.local.profile.LocalProfileDraftDataSource
import com.carenest.data.source.remote.dto.profile.EmergencyContactRequestDto
import com.carenest.data.source.remote.dto.profile.ProfileAllergyRequestDto
import com.carenest.data.source.remote.dto.profile.ProfileMedicalConditionRequestDto
import com.carenest.data.source.remote.dto.profile.ProfileRequestDto
import com.carenest.data.source.remote.service.ProfileApiException
import com.carenest.data.source.remote.service.ProfileApiService
import com.carenest.domain.model.profile.Allergy
import com.carenest.domain.model.profile.BasicHealthUpdate
import com.carenest.domain.model.profile.EmergencyContact
import com.carenest.domain.model.profile.EmergencyContactInput
import com.carenest.domain.model.profile.MedicalCondition
import com.carenest.domain.model.profile.MedicalHistoryUpdate
import com.carenest.domain.model.profile.PersonalInfoUpdate
import com.carenest.domain.model.profile.Profile
import com.carenest.domain.model.profile.ProfileAllergy
import com.carenest.domain.model.profile.ProfileException
import com.carenest.domain.model.profile.ProfileLocalDraft
import com.carenest.domain.model.profile.ProfileMedicalCondition
import com.carenest.domain.repository.ProfileRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val api: ProfileApiService,
    private val localDrafts: LocalProfileDraftDataSource
) : ProfileRepository {
    private var conditionBackendIdsByKey: Map<String, String> = emptyMap()
    private var allergyBackendIdsByKey: Map<String, String> = emptyMap()

    override suspend fun getDefaultProfile(): Result<Profile> =
        api.getDefaultProfile().mapCatching { it.toDomain() }.profileFailure()

    override suspend fun updatePersonalInfo(profileId: String, update: PersonalInfoUpdate): Result<Profile> =
        updateProfile(
            profileId,
            ProfileRequestDto(
                firstName = update.firstName,
                lastName = update.lastName,
                dateOfBirth = update.dateOfBirth,
                gender = update.gender
            )
        )

    override suspend fun updateBasicHealth(profileId: String, update: BasicHealthUpdate): Result<Profile> =
        updateProfile(
            profileId,
            ProfileRequestDto(
                height = update.height,
                weight = update.weight,
                bloodType = update.bloodType
            )
        )

    override suspend fun updateMedicalHistory(profileId: String, update: MedicalHistoryUpdate): Result<Profile> =
        updateProfile(
            profileId,
            ProfileRequestDto(
                previousSurgeries = update.previousSurgeries,
                previousHospitalizations = update.previousHospitalizations
            )
        )

    private suspend fun updateProfile(profileId: String, request: ProfileRequestDto): Result<Profile> =
        api.updateProfile(profileId, request)
            .mapCatching { it.toDomain() }
            .profileFailure()

    override suspend fun getMedicalConditionCatalog(): Result<List<MedicalCondition>> =
        api.getMedicalConditions()
            .mapCatching { list -> list.map { it.toDomain() } }
            .profileFailure()
            .onSuccess { catalog ->
                conditionBackendIdsByKey = catalog.associate { it.localKey to it.backendId }
            }

    override suspend fun getProfileMedicalConditions(profileId: String): Result<List<ProfileMedicalCondition>> =
        api.getProfileMedicalConditions(profileId)
            .mapCatching { list -> list.map { it.toDomain() } }
            .profileFailure()
            .onSuccess { saved ->
                conditionBackendIdsByKey = conditionBackendIdsByKey + saved.associate {
                    it.conditionName.normalizedCatalogKey() to it.medicalConditionId
                }
            }

    override suspend fun syncProfileMedicalConditions(
        profileId: String,
        originalBackendIds: Set<String>,
        selectedLocalKeys: Set<String>
    ): Result<Set<String>> {
        val unresolvedKeys = selectedLocalKeys - conditionBackendIdsByKey.keys
        if (unresolvedKeys.isNotEmpty()) {
            return Result.failure(ProfileException("Some medical conditions are no longer available. Please reload and try again."))
        }
        val selectedBackendIds = selectedLocalKeys.mapTo(mutableSetOf(), conditionBackendIdsByKey::getValue)
        return syncRelations(
            originalBackendIds,
            selectedBackendIds,
            add = { id ->
                api.addMedicalCondition(profileId, ProfileMedicalConditionRequestDto(id))
                    .mapCatching { it.toDomain() }
                    .profileFailure()
            },
            remove = { id -> api.removeMedicalCondition(profileId, id).profileFailure() }
        )
    }

    override suspend fun getAllergyCatalog(): Result<List<Allergy>> =
        api.getAllergies()
            .mapCatching { list -> list.map { it.toDomain() } }
            .profileFailure()
            .onSuccess { catalog ->
                allergyBackendIdsByKey = catalog.associate { it.localKey to it.backendId }
            }

    override suspend fun getProfileAllergies(profileId: String): Result<List<ProfileAllergy>> =
        api.getProfileAllergies(profileId)
            .mapCatching { list -> list.map { it.toDomain() } }
            .profileFailure()
            .onSuccess { saved ->
                allergyBackendIdsByKey = allergyBackendIdsByKey + saved.associate {
                    it.allergyName.normalizedCatalogKey() to it.allergyId
                }
            }

    override suspend fun syncProfileAllergies(
        profileId: String,
        originalBackendIds: Set<String>,
        selectedLocalKeys: Set<String>
    ): Result<Set<String>> {
        val unresolvedKeys = selectedLocalKeys - allergyBackendIdsByKey.keys
        if (unresolvedKeys.isNotEmpty()) {
            return Result.failure(ProfileException("Some allergies are no longer available. Please reload and try again."))
        }
        val selectedBackendIds = selectedLocalKeys.mapTo(mutableSetOf(), allergyBackendIdsByKey::getValue)
        return syncRelations(
            originalBackendIds,
            selectedBackendIds,
            add = { id ->
                api.addAllergy(profileId, ProfileAllergyRequestDto(id))
                    .mapCatching { it.toDomain() }
                    .profileFailure()
            },
            remove = { id -> api.removeAllergy(profileId, id).profileFailure() }
        )
    }

    override suspend fun getEmergencyContacts(profileId: String): Result<List<EmergencyContact>> =
        api.getEmergencyContacts(profileId)
            .mapCatching { list -> list.map { it.toDomain() } }
            .profileFailure()

    override suspend fun createEmergencyContact(
        profileId: String,
        input: EmergencyContactInput
    ): Result<EmergencyContact> =
        api.createEmergencyContact(profileId, input.toDto())
            .mapCatching { it.toDomain() }
            .profileFailure()

    override suspend fun updateEmergencyContact(
        emergencyContactId: String,
        input: EmergencyContactInput
    ): Result<EmergencyContact> =
        api.updateEmergencyContact(emergencyContactId, input.toDto())
            .mapCatching { it.toDomain() }
            .profileFailure()

    override suspend fun loadLocalDraft(userId: String): Result<ProfileLocalDraft> =
        runCatching { localDrafts.load(userId) }

    override suspend fun saveLocalDraft(userId: String, draft: ProfileLocalDraft): Result<Unit> =
        runCatching { localDrafts.save(userId, draft) }

    override suspend fun markHealthProfileOnboardingHandled(userId: String): Result<Unit> =
        runCatching { localDrafts.markOnboardingHandled(userId) }

    private fun EmergencyContactInput.toDto() =
        EmergencyContactRequestDto(contactName, relationship, phoneNumber)

    private suspend fun syncRelations(
        originalBackendIds: Set<String>,
        selectedBackendIds: Set<String>,
        add: suspend (String) -> Result<*>,
        remove: suspend (String) -> Result<Unit>
    ): Result<Set<String>> {
        for (id in (selectedBackendIds - originalBackendIds).sorted()) {
            val error = add(id).exceptionOrNull()
            if (error != null && !error.hasHttpStatus(409)) return Result.failure(error)
        }
        for (id in (originalBackendIds - selectedBackendIds).sorted()) {
            val error = remove(id).exceptionOrNull()
            if (error != null && !error.hasHttpStatus(404)) return Result.failure(error)
        }
        return Result.success(selectedBackendIds)
    }
}

private fun Throwable.toDomainFailure(): Throwable = when (this) {
    is ProfileException -> this
    is ProfileApiException -> ProfileException(message ?: "Profile request failed", statusCode, backendCode)
    else -> this
}

private fun <T> Result<T>.profileFailure(): Result<T> =
    exceptionOrNull()?.let { Result.failure(it.toDomainFailure()) } ?: this

private fun Throwable.hasHttpStatus(statusCode: Int): Boolean =
    (this as? ProfileException)?.statusCode == statusCode

private fun String.normalizedCatalogKey(): String =
    trim().lowercase().replace(Regex("[^a-z0-9]+"), "-").trim('-')
