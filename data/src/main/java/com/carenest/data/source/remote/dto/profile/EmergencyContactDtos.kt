package com.carenest.data.source.remote.dto.profile

import kotlinx.serialization.Serializable

@Serializable
data class EmergencyContactRequestDto(
    val contactName: String,
    val relationship: String? = null,
    val phoneNumber: String
)

@Serializable
data class EmergencyContactResponseDto(
    val id: String? = null,
    val profileId: String? = null,
    val contactName: String? = null,
    val relationship: String? = null,
    val phoneNumber: String? = null
)
