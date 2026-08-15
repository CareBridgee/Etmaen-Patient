package com.carenest.domain.model.home

data class User(
    val id: String = "",
    val phoneNumber: String = "",
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
    val credit: Double = 0.0,
) {
    val name: String?
        get() = listOfNotNull(firstName, lastName)
            .joinToString(" ")
            .trim()
            .takeIf(String::isNotBlank)

    val avatarUrl: String?
        get() = profileImageUrl

    val hasCompletePersonalInformation: Boolean
        get() = !firstName.isNullOrBlank() &&
            !lastName.isNullOrBlank() &&
            !dateOfBirth.isNullOrBlank() &&
            !gender.isNullOrBlank()
}
