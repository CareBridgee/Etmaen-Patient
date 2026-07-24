package com.carenest.data.mapper.profile

import com.carenest.data.source.remote.dto.profile.ProfileResponseDto
import com.carenest.domain.model.profile.Profile

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
