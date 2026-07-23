package com.carenest.data.source.remote.dto.profile

import kotlinx.serialization.Serializable

@Serializable data class ProfileMedicalConditionRequestDto(val medicalConditionId: String)
@Serializable data class ProfileMedicalConditionResponseDto(
    val id: String? = null,
    val profileId: String? = null,
    val medicalConditionId: String? = null,
    val conditionName: String? = null
)

@Serializable data class ProfileAllergyRequestDto(val allergyId: String)
@Serializable data class ProfileAllergyResponseDto(
    val id: String? = null,
    val profileId: String? = null,
    val allergyId: String? = null,
    val allergyName: String? = null,
    val allergyType: String? = null
)

@Serializable data class ProfileMedicationRequestDto(val medicationId: String)
@Serializable data class ProfileMedicationResponseDto(
    val id: String? = null,
    val profileId: String? = null,
    val medicationId: String? = null,
    val medicationName: String? = null
)
