package com.carenest.domain.model.family_members

data class FamilyMember(
    val id: String,
    val profileId: String? = null,
    val contactName: String,
    val relationship: String? = null,
    val phoneNumber: String,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

data class FamilyMemberInput(
    val contactName: String,
    val relationship: String?,
    val phoneNumber: String
)
