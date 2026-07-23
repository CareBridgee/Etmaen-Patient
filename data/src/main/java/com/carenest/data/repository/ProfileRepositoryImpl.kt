package com.carenest.data.repository

import com.carenest.data.mapper.profile.*
import com.carenest.data.source.remote.dto.profile.*
import com.carenest.data.source.remote.service.ProfileApiException
import com.carenest.data.source.remote.service.ProfileApiService
import com.carenest.data.source.local.profile.LocalProfileDraftDataSource
import com.carenest.data.source.local.profile.ProfileFallbackCatalogDataSource
import com.carenest.domain.model.profile.*
import com.carenest.domain.repository.ProfileRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val api: ProfileApiService,
    private val fallbackCatalog: ProfileFallbackCatalogDataSource,
    private val localDrafts: LocalProfileDraftDataSource
) : ProfileRepository {
    private var conditionBackendIdsByKey: Map<String, String> = emptyMap()
    private var allergyBackendIdsByKey: Map<String, String> = emptyMap()
    private var medicationBackendIdsByKey: Map<String, String> = emptyMap()

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
        updateProfile(profileId, ProfileRequestDto(height = update.height, weight = update.weight, bloodType = update.bloodType))

    override suspend fun updateMedicalHistory(profileId: String, update: MedicalHistoryUpdate): Result<Profile> =
        updateProfile(
            profileId,
            ProfileRequestDto(
                previousSurgeries = update.previousSurgeries,
                previousHospitalizations = update.previousHospitalizations
            )
        )

    override suspend fun updateMobility(profileId: String, update: MobilityUpdate): Result<Profile> =
        updateProfile(profileId, ProfileRequestDto(mobilityStatus = update.mobilityStatus, mobilityNotes = update.mobilityNotes))

    private suspend fun updateProfile(profileId: String, request: ProfileRequestDto): Result<Profile> =
        api.updateProfile(profileId, request)
            .mapCatching { it.toDomain() }
            .profileFailure()

    override suspend fun getMedicalConditionCatalog(): Result<List<MedicalCondition>> {
        val remote = api.getMedicalConditions().mapCatching { list -> list.map { it.toDomain() } }.getOrNull()
        conditionBackendIdsByKey = remote.orEmpty().mapNotNull { item ->
            item.backendId?.let { item.localKey to it }
        }.toMap()
        return Result.success(remote?.takeIf { it.isNotEmpty() } ?: fallbackCatalog.medicalConditions())
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
        val selectedBackendIds = selectedLocalKeys.mapNotNull(conditionBackendIdsByKey::get).toSet()
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

    override suspend fun getAllergyCatalog(): Result<List<Allergy>> {
        val remote = api.getAllergies().mapCatching { list -> list.map { it.toDomain() } }.getOrNull()
        allergyBackendIdsByKey = remote.orEmpty().mapNotNull { item ->
            item.backendId?.let { item.localKey to it }
        }.toMap()
        return Result.success(remote?.takeIf { it.isNotEmpty() } ?: fallbackCatalog.allergies())
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
        val selectedBackendIds = selectedLocalKeys.mapNotNull(allergyBackendIdsByKey::get).toSet()
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

    override suspend fun getMedicationCatalog(): Result<List<Medication>> {
        val remote = api.getMedications().mapCatching { list -> list.map { it.toDomain() } }.getOrNull()
        medicationBackendIdsByKey = remote.orEmpty().mapNotNull { item ->
            item.backendId?.let { item.localKey to it }
        }.toMap()
        return Result.success(remote?.takeIf { it.isNotEmpty() } ?: fallbackCatalog.medications())
    }

    override suspend fun getProfileMedications(profileId: String): Result<List<ProfileMedication>> =
        api.getProfileMedications(profileId)
            .mapCatching { list -> list.map { it.toDomain() } }
            .profileFailure()
            .onSuccess { saved ->
                medicationBackendIdsByKey = medicationBackendIdsByKey + saved.associate {
                    it.medicationName.normalizedCatalogKey() to it.medicationId
                }
            }

    override suspend fun syncProfileMedications(
        profileId: String,
        originalBackendIds: Set<String>,
        entries: List<LocalMedicationEntry>
    ): Result<List<LocalMedicationEntry>> {
        val resolved = entries.map { entry ->
            val backendId = entry.backendMedicationId
                ?: medicationBackendIdsByKey[entry.name.normalizedCatalogKey()]
            entry.copy(
                backendMedicationId = backendId,
                syncState = if (backendId == null) SyncState.LOCAL_ONLY else SyncState.PENDING
            )
        }
        val selectedBackendIds = resolved.mapNotNull(LocalMedicationEntry::backendMedicationId).toSet()
        return syncRelations(
            originalBackendIds,
            selectedBackendIds,
            add = { id ->
                api.addMedication(profileId, ProfileMedicationRequestDto(id))
                    .mapCatching { it.toDomain() }
                    .profileFailure()
            },
            remove = { id -> api.removeMedication(profileId, id).profileFailure() }
        ).map {
            resolved.map { entry ->
                if (entry.backendMedicationId == null) entry else entry.copy(syncState = SyncState.SYNCED)
            }
        }
    }

    override suspend fun getEmergencyContacts(profileId: String) =
        api.getEmergencyContacts(profileId).mapCatching { list -> list.map { it.toDomain() } }.profileFailure()

    override suspend fun createEmergencyContact(profileId: String, input: EmergencyContactInput) =
        api.createEmergencyContact(profileId, input.toDto()).mapCatching { it.toDomain() }.profileFailure()

    override suspend fun updateEmergencyContact(emergencyContactId: String, input: EmergencyContactInput) =
        api.updateEmergencyContact(emergencyContactId, input.toDto()).mapCatching { it.toDomain() }.profileFailure()

    override suspend fun loadLocalDraft(userId: String): Result<ProfileLocalDraft> =
        runCatching { localDrafts.load(userId) }

    override suspend fun saveLocalDraft(userId: String, draft: ProfileLocalDraft): Result<Unit> =
        runCatching { localDrafts.save(userId, draft) }

    override suspend fun markHealthProfileOnboardingHandled(userId: String): Result<Unit> =
        runCatching { localDrafts.markOnboardingHandled(userId) }

    private fun EmergencyContactInput.toDto() = EmergencyContactRequestDto(contactName, relationship, phoneNumber)

    private suspend fun syncRelations(
        originalBackendIds: Set<String>,
        selectedBackendIds: Set<String>,
        add: suspend (String) -> Result<*>,
        remove: suspend (String) -> Result<Unit>
    ): Result<Set<String>> {
        for (id in (selectedBackendIds - originalBackendIds).sorted()) {
            add(id).getOrElse { return Result.failure(it) }
        }
        for (id in (originalBackendIds - selectedBackendIds).sorted()) {
            remove(id).getOrElse { return Result.failure(it) }
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

private fun String.normalizedCatalogKey(): String =
    trim().lowercase().replace(Regex("[^a-z0-9]+"), "-").trim('-')
