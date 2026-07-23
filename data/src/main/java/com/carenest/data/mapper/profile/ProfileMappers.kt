package com.carenest.data.mapper.profile

import com.carenest.data.source.remote.dto.profile.AllergyDto
import com.carenest.data.source.remote.dto.profile.EmergencyContactResponseDto
import com.carenest.data.source.remote.dto.profile.MedicalConditionDto
import com.carenest.data.source.remote.dto.profile.ProfileAllergyResponseDto
import com.carenest.data.source.remote.dto.profile.ProfileMedicalConditionResponseDto
import com.carenest.data.source.remote.dto.profile.ProfileResponseDto
import com.carenest.domain.model.profile.Allergy
import com.carenest.domain.model.profile.AllergyType
import com.carenest.domain.model.profile.EmergencyContact
import com.carenest.domain.model.profile.MedicalCondition
import com.carenest.domain.model.profile.Profile
import com.carenest.domain.model.profile.ProfileAllergy
import com.carenest.domain.model.profile.ProfileException
import com.carenest.domain.model.profile.ProfileMedicalCondition
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
    id = id.requiredUuid("medical condition id"),
    name = name.requiredText("medical condition name"),
    description = description
)

internal fun ProfileMedicalConditionResponseDto.toDomain() = ProfileMedicalCondition(
    medicalConditionId = medicalConditionId.requiredUuid("medical condition id"),
    conditionName = conditionName.requiredText("medical condition name")
)

internal fun AllergyDto.toDomain() = Allergy(
    id = id.requiredUuid("allergy id"),
    name = name.requiredText("allergy name"),
    type = type.toAllergyType()
)

internal fun ProfileAllergyResponseDto.toDomain() = ProfileAllergy(
    allergyId = allergyId.requiredUuid("allergy id"),
    allergyName = allergyName.requiredText("allergy name"),
    type = allergyType.toAllergyType()
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
