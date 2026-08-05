package com.carenest.data.source.remote.dto.family_members

import kotlinx.serialization.Serializable

@Serializable
data class FamilyMemberRequestDto(
    val contactName: String,
    val relationship: String? = null,
    val phoneNumber: String
)

@Serializable
data class FamilyMemberResponseDto(
    val id: String? = null,
    val profileId: String? = null,
    val contactName: String? = null,
    val relationship: String? = null,
    val phoneNumber: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)
