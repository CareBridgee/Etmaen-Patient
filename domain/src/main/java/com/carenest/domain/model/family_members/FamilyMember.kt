package com.carenest.domain.model.family_members

data class FamilyMember(
    val id: String,
    val relationship: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val phoneNumber: String? = null,
    val dateOfBirth: String? = null,
    val gender: String? = null,
    val bloodType: String? = null,
    val height: Double? = null,
    val weight: Double? = null,
    val mobilityStatus: String? = null,
    val mobilityNotes: String? = null,
    val previousSurgeries: String? = null,
    val previousHospitalizations: String? = null,
    val profileImageUrl: String? = null,
    val isPrimary: Boolean = false,
    val isDeleted: Boolean = false,
    val createdAt: String? = null,
    val updatedAt: String? = null
) {
    val fullName: String
        get() {
            val name = listOfNotNull(firstName, lastName)
                .filter { it.isNotBlank() }
                .joinToString(" ")
            return name.ifBlank { relationship ?: "Family Member" }
        }
}

data class FamilyMemberInput(
    val relationship: String?,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val dateOfBirth: String,
    val gender: String,
    val bloodType: String? = null,
    val height: Double? = null,
    val weight: Double? = null,
    val mobilityStatus: String? = null,
    val mobilityNotes: String? = null,
    val previousSurgeries: String? = null,
    val previousHospitalizations: String? = null,
    val profileImageUrl: String? = null
)
