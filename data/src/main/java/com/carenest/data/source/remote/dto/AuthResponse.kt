package com.carenest.data.source.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long,
    val user: UserDto
)

@Serializable
data class UserDto(
    val id: String,
    val phoneNumber: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val dateOfBirth: String? = null,
    val gender: String? = null,
    val profileImageUrl: String? = null,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val lastLoginAt: String? = null,
    val defaultProfileId: String? = null
)
