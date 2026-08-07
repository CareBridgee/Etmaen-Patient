package com.carenest.data.source.remote.dto.profile

import kotlinx.serialization.Serializable

@Serializable
data class ProfileMedicationRequestDto(
    val medicationId: String? = null,
    val name: String? = null
)

@Serializable
data class ProfileMedicationResponseDto(
    val id: String? = null,
    val profileId: String? = null,
    val medicationId: String? = null,
    val medicationName: String? = null
)
