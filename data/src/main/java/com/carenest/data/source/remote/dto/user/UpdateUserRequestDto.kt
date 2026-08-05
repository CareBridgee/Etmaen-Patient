package com.carenest.data.source.remote.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserRequestDto(
    val firstName: String,
    val lastName: String,
    val email: String? = null,
    val dateOfBirth: String? = null,
    val gender: String? = null,
    val profileImageUrl: String? = null
)
