package com.carenest.data.source.remote.dto.profile

import kotlinx.serialization.Serializable

@Serializable
data class MedicalConditionDto(
    val id: String? = null,
    val name: String? = null,
    val description: String? = null
)

@Serializable
data class ProfileMedicalConditionRequestDto(
    val medicalConditionId: String
)

@Serializable
data class ProfileMedicalConditionResponseDto(
    val id: String? = null,
    val profileId: String? = null,
    val medicalConditionId: String? = null,
    val conditionName: String? = null
)
