package com.carenest.domain.model.profile

data class EmergencyContact(
    val id: String,
    val profileId: String?,
    val contactName: String,
    val relationship: String?,
    val phoneNumber: String
)

data class EmergencyContactInput(
    val contactName: String,
    val relationship: String?,
    val phoneNumber: String
)
