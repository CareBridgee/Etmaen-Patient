package com.carenest.domain.model.user

data class UserUpdate(
    val firstName: String,
    val lastName: String,
    val email: String? = null,
    val dateOfBirth: String? = null,
    val gender: String? = null,
    val profileImageUrl: String? = null
)
