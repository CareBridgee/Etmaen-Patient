package com.carenest.data.mapper.profile

import com.carenest.data.source.remote.dto.profile.*
import com.carenest.domain.model.profile.*
import java.util.UUID

internal fun ProfileResponseDto.toDomain(): Profile = Profile(
    id = id.requiredUuid("profile id"),
    userId = userId,
    relationship = relationship,
    firstName = firstName,
    lastName = lastName,
    dateOfBirth = dateOfBirth,
    gender = gender,
    bloodType = bloodType,
    height = height,
    weight = weight,
    mobilityStatus = mobilityStatus,
    mobilityNotes = mobilityNotes,
    previousSurgeries = previousSurgeries,
    previousHospitalizations = previousHospitalizations
)

internal fun MedicalConditionDto.toDomain() = MedicalCondition(
    localKey = name.requiredText("medical condition name").normalizedCatalogKey(),
    backendId = id.requiredUuid("medical condition id"),
    name = name.requiredText("medical condition name"),
    description = description,
    source = CatalogSource.REMOTE,
    syncState = SyncState.SYNCED
)

internal fun ProfileMedicalConditionResponseDto.toDomain() = ProfileMedicalCondition(
    medicalConditionId = medicalConditionId.requiredUuid("medical condition id"),
    conditionName = conditionName.requiredText("medical condition name")
)

internal fun AllergyDto.toDomain() = Allergy(
    localKey = name.requiredText("allergy name").normalizedCatalogKey(),
    backendId = id.requiredUuid("allergy id"),
    name = name.requiredText("allergy name"),
    type = type.toAllergyType(),
    source = CatalogSource.REMOTE,
    syncState = SyncState.SYNCED
)

internal fun ProfileAllergyResponseDto.toDomain() = ProfileAllergy(
    allergyId = allergyId.requiredUuid("allergy id"),
    allergyName = allergyName.requiredText("allergy name"),
    type = allergyType.toAllergyType()
)

internal fun MedicationDto.toDomain() = Medication(
    localKey = name.requiredText("medication name").normalizedCatalogKey(),
    backendId = id.requiredUuid("medication id"),
    name = name.requiredText("medication name"),
    source = CatalogSource.REMOTE,
    syncState = SyncState.SYNCED
)

internal fun ProfileMedicationResponseDto.toDomain() = ProfileMedication(
    medicationId = medicationId.requiredUuid("medication id"),
    medicationName = medicationName.requiredText("medication name")
)

internal fun EmergencyContactResponseDto.toDomain() = EmergencyContact(
    id = id.requiredUuid("emergency contact id"),
    profileId = profileId.optionalUuid("emergency contact profile id"),
    contactName = contactName.requiredText("emergency contact name"),
    relationship = relationship,
    phoneNumber = phoneNumber.requiredText("emergency contact phone number")
)

private fun String?.requiredUuid(label: String): String {
    val value = requiredText(label)
    if (runCatching { UUID.fromString(value) }.isFailure) {
        throw ProfileException("Backend returned an invalid $label")
    }
    return value
}

private fun String?.requiredText(label: String): String =
    this?.takeIf(String::isNotBlank) ?: throw ProfileException("Backend returned a missing $label")

private fun String?.optionalUuid(label: String): String? {
    val value = this?.trim()?.takeIf(String::isNotEmpty) ?: return null
    if (runCatching { UUID.fromString(value) }.isFailure) {
        throw ProfileException("Backend returned an invalid $label")
    }
    return value
}

private fun String?.toAllergyType(): AllergyType = when (
    this?.trim()?.uppercase()?.replace('-', '_')?.replace(' ', '_')
) {
    "DRUG", "DRUG_ALLERGY" -> AllergyType.DRUG
    "FOOD", "FOOD_ALLERGY" -> AllergyType.FOOD
    else -> AllergyType.OTHER
}

private fun String.normalizedCatalogKey(): String =
    trim().lowercase().replace(Regex("[^a-z0-9]+"), "-").trim('-')
