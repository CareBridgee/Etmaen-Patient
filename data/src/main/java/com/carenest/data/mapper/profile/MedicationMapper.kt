package com.carenest.data.mapper.profile

import com.carenest.data.source.remote.dto.profile.ProfileMedicationResponseDto
import com.carenest.domain.model.profile.ProfileMedication

internal fun ProfileMedicationResponseDto.toDomain(): ProfileMedication = ProfileMedication(
    id = id.requiredUuid("profile medication id"),
    profileId = profileId.requiredUuid("medication profile id"),
    medicationId = medicationId,
    name = medicationName.orEmpty()
)
