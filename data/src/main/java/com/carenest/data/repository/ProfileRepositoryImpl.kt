package com.carenest.data.repository

import com.carenest.data.mapper.profile.toDomain
import com.carenest.data.source.remote.dto.profile.EmergencyContactRequestDto
import com.carenest.data.source.remote.dto.profile.ProfileAllergyRequestDto
import com.carenest.data.source.remote.dto.profile.ProfileMedicalConditionRequestDto
import com.carenest.data.source.remote.dto.profile.ProfileMedicationRequestDto
import com.carenest.data.source.remote.dto.profile.ProfileRequestDto
import com.carenest.data.source.remote.ApiException
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
import com.carenest.domain.model.profile.ProfileMedicalCondition
import com.carenest.domain.model.profile.ProfileMedication
import com.carenest.domain.repository.ProfileRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val api: ProfileApiService
) : ProfileRepository {

    override suspend fun getProfileMedications(profileId: String): Result<List<ProfileMedication>> =
        api.getProfileMedications(profileId)
            .mapCatching { list -> list.map { it.toDomain() } }
            .profileFailure()

    override suspend fun syncProfileMedications(
        profileId: String,
        names: List<String>
    ): Result<List<String>> {
        val current = getProfileMedications(profileId).getOrElse { return Result.failure(it) }
        val requested = names.map(String::trim).filter(String::isNotBlank)
            .distinctBy(String::normalizedName)
        val requestedKeys = requested.mapTo(mutableSetOf(), String::normalizedName)
        current.filter { it.name.normalizedName() !in requestedKeys }.forEach { medication ->
            val relationId = medication.medicationId ?: medication.id
            api.removeProfileMedication(profileId, relationId).profileFailure()
                .getOrElse { return Result.failure(it) }
        }
        val currentKeys = current.mapTo(mutableSetOf()) { it.name.normalizedName() }
        requested.filter { it.normalizedName() !in currentKeys }.forEach { name ->
            api.addProfileMedication(profileId, ProfileMedicationRequestDto(name = name))
                .profileFailure().getOrElse { return Result.failure(it) }
        }
        return Result.success(requested)
    }

    override suspend fun getDefaultProfile(): Result<Profile> =
        api.getDefaultProfile().mapCatching { it.toDomain() }.profileFailure()

    override suspend fun getProfile(profileId: String): Result<Profile> =
        api.getProfile(profileId).mapCatching { it.toDomain() }.profileFailure()
    
  override suspend fun getProfiles(): Result<List<Profile>> =
        api.getProfiles().mapCatching { list -> list.map { it.toDomain() } }.profileFailure()

    override suspend fun createFamilyMember(
        relationship: String,
        firstName: String,
        lastName: String,
        dateOfBirth: String,
        gender: String
    ): Result<Profile> = api.createProfile(
        ProfileRequestDto(
            relationship = relationship,
            firstName = firstName,
            lastName = lastName,
            dateOfBirth = dateOfBirth,
            gender = gender
        )
    ).mapCatching { it.toDomain() }.profileFailure()

    override suspend fun updatePersonalInfo(
        profileId: String,
        update: PersonalInfoUpdate
    ): Result<Profile> = updateProfile(
        profileId,
        ProfileRequestDto(
            firstName = update.firstName,
            lastName = update.lastName,
            dateOfBirth = update.dateOfBirth,
            gender = update.gender
        )
    )

    override suspend fun updateBasicHealth(
        profileId: String,
        update: BasicHealthUpdate
    ): Result<Profile> = updateProfile(
        profileId,
        ProfileRequestDto(
            height = update.height,
            weight = update.weight,
            bloodType = update.bloodType
        )
    )

    override suspend fun updateMedicalHistory(
        profileId: String,
        update: MedicalHistoryUpdate
    ): Result<Profile> = updateProfile(
        profileId,
        ProfileRequestDto(
            previousSurgeries = update.previousSurgeries,
            previousHospitalizations = update.previousHospitalizations
        )
    )

    override suspend fun updateMobility(
        profileId: String,
        mobilityStatus: String,
        mobilityNotes: String
    ): Result<Profile> = updateProfile(
        profileId,
        ProfileRequestDto(
            mobilityStatus = mobilityStatus,
            mobilityNotes = mobilityNotes
        )
    )

    private suspend fun updateProfile(
        profileId: String,
        request: ProfileRequestDto
    ): Result<Profile> = api.updateProfile(profileId, request)
        .mapCatching { it.toDomain() }
        .profileFailure()

    override suspend fun getMedicalConditionCatalog(): Result<List<MedicalCondition>> =
        api.getMedicalConditions()
            .mapCatching { list -> list.map { it.toDomain() } }
            .profileFailure()

    override suspend fun getProfileMedicalConditions(
        profileId: String
    ): Result<List<ProfileMedicalCondition>> = api.getProfileMedicalConditions(profileId)
        .mapCatching { list -> list.map { it.toDomain() } }
        .profileFailure()

    override suspend fun syncProfileMedicalConditions(
        profileId: String,
        originalBackendIds: Set<String>,
        selectedBackendIds: Set<String>
    ): Result<Set<String>> = syncRelations(
        originalBackendIds,
        selectedBackendIds,
        add = { id ->
            api.addMedicalCondition(
                profileId,
                ProfileMedicalConditionRequestDto(medicalConditionId = id)
            )
                .mapCatching { it.toDomain() }
                .profileFailure()
        },
        remove = { id -> api.removeMedicalCondition(profileId, id).profileFailure() }
    )

    override suspend fun syncProfileMedicalConditionsByName(
        profileId: String,
        names: List<String>
    ): Result<Unit> {
        val current = getProfileMedicalConditions(profileId)
            .getOrElse { return Result.failure(it) }
        return syncNamedRelations(
            current = current.map { it.medicalConditionId to it.conditionName },
            requestedNames = names,
            add = { name ->
                api.addMedicalCondition(
                    profileId,
                    ProfileMedicalConditionRequestDto(name = name)
                ).profileFailure()
            },
            remove = { id -> api.removeMedicalCondition(profileId, id).profileFailure() }
        )
    }

    override suspend fun addCustomMedicalCondition(
        profileId: String,
        name: String
    ): Result<ProfileMedicalCondition> = api.addMedicalCondition(
        profileId,
        ProfileMedicalConditionRequestDto(name = name)
    ).mapCatching { it.toDomain() }.profileFailure()

    override suspend fun getAllergyCatalog(): Result<List<Allergy>> =
        api.getAllergies()
            .mapCatching { list -> list.map { it.toDomain() } }
            .profileFailure()

    override suspend fun getProfileAllergies(profileId: String): Result<List<ProfileAllergy>> =
        api.getProfileAllergies(profileId)
            .mapCatching { list -> list.map { it.toDomain() } }
            .profileFailure()

    override suspend fun syncProfileAllergies(
        profileId: String,
        originalBackendIds: Set<String>,
        selectedBackendIds: Set<String>
    ): Result<Set<String>> = syncRelations(
        originalBackendIds,
        selectedBackendIds,
        add = { id ->
            api.addAllergy(profileId, ProfileAllergyRequestDto(allergyId = id))
                .mapCatching { it.toDomain() }
                .profileFailure()
        },
        remove = { id -> api.removeAllergy(profileId, id).profileFailure() }
    )

    override suspend fun syncProfileAllergiesByName(
        profileId: String,
        names: List<String>
    ): Result<Unit> {
        val current = getProfileAllergies(profileId).getOrElse { return Result.failure(it) }
        return syncNamedRelations(
            current = current.map { it.allergyId to it.allergyName },
            requestedNames = names,
            add = { name ->
                api.addAllergy(profileId, ProfileAllergyRequestDto(name = name)).profileFailure()
            },
            remove = { id -> api.removeAllergy(profileId, id).profileFailure() }
        )
    }

    override suspend fun addCustomAllergy(
        profileId: String,
        name: String
    ): Result<ProfileAllergy> = api.addAllergy(
        profileId,
        ProfileAllergyRequestDto(name = name)
    ).mapCatching { it.toDomain() }.profileFailure()

    override suspend fun getEmergencyContacts(profileId: String): Result<List<EmergencyContact>> =
        api.getEmergencyContacts(profileId)
            .mapCatching { list -> list.map { it.toDomain() } }
            .profileFailure()

    override suspend fun getEmergencyContactById(emergencyContactId: String): Result<EmergencyContact> =
        api.getEmergencyContactById(emergencyContactId)
            .mapCatching { it.toDomain() }
            .profileFailure()

    override suspend fun createEmergencyContact(
        profileId: String,
        input: EmergencyContactInput
    ): Result<EmergencyContact> = api.createEmergencyContact(profileId, input.toDto())
        .mapCatching { it.toDomain() }
        .profileFailure()

    override suspend fun updateEmergencyContact(
        emergencyContactId: String,
        input: EmergencyContactInput
    ): Result<EmergencyContact> = api.updateEmergencyContact(emergencyContactId, input.toDto())
        .mapCatching { it.toDomain() }
        .profileFailure()

    override suspend fun deleteEmergencyContact(emergencyContactId: String): Result<Unit> =
        api.deleteEmergencyContact(emergencyContactId)
            .profileFailure()

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

    private suspend fun syncNamedRelations(
        current: List<Pair<String, String>>,
        requestedNames: List<String>,
        add: suspend (String) -> Result<*>,
        remove: suspend (String) -> Result<Unit>
    ): Result<Unit> {
        val requested = requestedNames.map(String::trim).filter(String::isNotBlank)
            .distinctBy(String::normalizedName)
        val currentKeys = current.mapTo(hashSetOf()) { (_, name) -> name.normalizedName() }
        for (name in requested.filter { it.normalizedName() !in currentKeys }) {
            val error = add(name).exceptionOrNull()
            if (error != null && !error.hasHttpStatus(409)) return Result.failure(error)
        }

        val requestedKeys = requested.mapTo(hashSetOf(), String::normalizedName)
        for ((id, name) in current.filter { (_, name) -> name.normalizedName() !in requestedKeys }) {
            val error = remove(id).exceptionOrNull()
            if (error != null && !error.hasHttpStatus(404)) return Result.failure(error)
        }
        return Result.success(Unit)
    }
}

internal fun Throwable.toDomainFailure(): Throwable = when (this) {
    is ProfileException -> this
    is ApiException -> ProfileException(message ?: "Profile request failed", statusCode, backendCode)
    else -> this
}

internal fun <T> Result<T>.profileFailure(): Result<T> =
    exceptionOrNull()?.let { Result.failure(it.toDomainFailure()) } ?: this

private fun Throwable.hasHttpStatus(statusCode: Int): Boolean =
    (this as? ProfileException)?.statusCode == statusCode

private fun String.normalizedName(): String = trim().lowercase()
