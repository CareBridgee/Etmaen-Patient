package com.carenest.domain.model.profile

data class Profile(
    val id: String,
    val userId: String?,
    val relationship: String?,
    val firstName: String?,
    val lastName: String?,
    val dateOfBirth: String?,
    val gender: String?,
    val bloodType: String?,
    val height: Double?,
    val weight: Double?,
    val mobilityStatus: String?,
    val mobilityNotes: String?,
    val previousSurgeries: String?,
    val previousHospitalizations: String?,
    val isPrimary: Boolean = false,
    val isDeleted: Boolean = false
)

data class PersonalInfoUpdate(
    val firstName: String,
    val lastName: String,
    val dateOfBirth: String,
    val gender: String
)

data class BasicHealthUpdate(
    val height: Double,
    val weight: Double,
    val bloodType: String
)

data class MedicalHistoryUpdate(
    val previousSurgeries: String,
    val previousHospitalizations: String
)

data class MobilityInput(
    val status: MobilityStatus,
    val notes: String
)
