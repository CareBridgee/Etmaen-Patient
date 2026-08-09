package com.carenest.data.mapper.family_members

import com.carenest.data.source.remote.dto.profile.ProfileRequestDto
import com.carenest.data.source.remote.dto.profile.ProfileResponseDto
import com.carenest.domain.model.family_members.FamilyMember
import com.carenest.domain.model.family_members.FamilyMemberInput

internal fun ProfileResponseDto.toDomain(): FamilyMember = FamilyMember(
    id = id.orEmpty(),
    relationship = relationship,
    firstName = firstName,
    lastName = lastName,
    phoneNumber = phoneNumber,
    dateOfBirth = dateOfBirth,
    gender = gender,
    bloodType = bloodType,
    height = height,
    weight = weight,
    mobilityStatus = mobilityStatus,
    mobilityNotes = mobilityNotes,
    previousSurgeries = previousSurgeries,
    previousHospitalizations = previousHospitalizations,
    profileImageUrl = profileImageUrl,
    isPrimary = isPrimary ?: false,
    isDeleted = isDeleted ?: false,
    createdAt = createdAt,
    updatedAt = updatedAt
)

internal fun FamilyMemberInput.toDto(): ProfileRequestDto = ProfileRequestDto(
    relationship = relationship,
    firstName = firstName,
    lastName = lastName,
    phoneNumber = phoneNumber.ifBlank { null },
    dateOfBirth = dateOfBirth,
    gender = gender,
    bloodType = bloodType,
    height = height,
    weight = weight,
    mobilityStatus = mobilityStatus,
    mobilityNotes = mobilityNotes,
    previousSurgeries = previousSurgeries,
    previousHospitalizations = previousHospitalizations,
    profileImageUrl = profileImageUrl
)
