package com.carenest.data.source.remote.dto.profile

import kotlinx.serialization.Serializable

@Serializable
data class AllergyDto(
    val id: String? = null,
    val name: String? = null,
    val type: String? = null
)

@Serializable
data class ProfileAllergyRequestDto(
    val allergyId: String? = null,
    val name: String? = null,
    val type: String? = null
)

@Serializable
data class ProfileAllergyResponseDto(
    val id: String? = null,
    val profileId: String? = null,
    val allergyId: String? = null,
    val allergyName: String? = null,
    val allergyType: String? = null
)
