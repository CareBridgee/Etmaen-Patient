package com.carenest.data.source.remote.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class UserResponseDto(
    val id: String? = null,
    val phoneNumber: String? = null,
    val email: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val dateOfBirth: String? = null,
    val gender: String? = null,
    val profileImageUrl: String? = null,
    val isDeleted: Boolean = false,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val lastLoginAt: String? = null,
    val defaultProfileId: String? = null,
    val credit: Double? = null,
)
