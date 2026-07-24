package com.carenest.data.mapper.profile

import com.carenest.data.source.remote.dto.profile.MedicalConditionDto
import com.carenest.data.source.remote.dto.profile.ProfileMedicalConditionResponseDto
import com.carenest.domain.model.profile.MedicalCondition
import com.carenest.domain.model.profile.ProfileMedicalCondition

internal fun MedicalConditionDto.toDomain(): MedicalCondition = MedicalCondition(
    id = id.requiredUuid("medical condition id"),
    name = name.requiredText("medical condition name"),
    description = description
)

internal fun ProfileMedicalConditionResponseDto.toDomain(): ProfileMedicalCondition =
    ProfileMedicalCondition(
        medicalConditionId = medicalConditionId.requiredUuid("medical condition id"),
        conditionName = conditionName.requiredText("medical condition name")
    )
