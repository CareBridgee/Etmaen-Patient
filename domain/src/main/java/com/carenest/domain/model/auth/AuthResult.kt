package com.carenest.domain.model.auth

data class AuthResult(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long,
    val user: User
)

data class User(
    val id: String,
    val phoneNumber: String,
    val firstName: String?,
    val lastName: String?,
    val dateOfBirth: String?,
    val gender: String?,
    val profileImageUrl: String?,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val lastLoginAt: String?,
    val defaultProfileId: String?
)
