package com.carenest.domain.model

data class Patient(
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
