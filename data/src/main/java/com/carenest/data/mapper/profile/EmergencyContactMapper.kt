package com.carenest.data.mapper.profile

import com.carenest.data.source.remote.dto.profile.EmergencyContactResponseDto
import com.carenest.domain.model.profile.EmergencyContact
import com.carenest.domain.model.profile.Profile

internal fun EmergencyContactResponseDto.toDomain(): EmergencyContact = EmergencyContact(
    id = id ?: java.util.UUID.randomUUID().toString(),
    profileId = profileId,
    contactName = contactName ?: "",
    relationship = relationship,
    phoneNumber = phoneNumber ?: ""
)

internal fun EmergencyContactResponseDto.toProfileDomain(): Profile = Profile(
    id = id ?: java.util.UUID.randomUUID().toString(),
    userId = profileId,
    relationship = relationship,
    firstName = contactName,
    lastName = null,
    dateOfBirth = null,
    gender = null,
    bloodType = null,
    height = null,
    weight = null,
    mobilityStatus = null,
    mobilityNotes = null,
    previousSurgeries = null,
    previousHospitalizations = null
)
