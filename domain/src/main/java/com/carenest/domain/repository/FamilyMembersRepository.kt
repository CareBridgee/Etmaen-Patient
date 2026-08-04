package com.carenest.domain.repository

import com.carenest.domain.model.family_members.FamilyMember

interface FamilyMembersRepository {
    suspend fun getFamilyMembers(): Result<List<FamilyMember>>
    suspend fun getFamilyMemberById(id: String): Result<FamilyMember>
    suspend fun createFamilyMember(
        relationship: String,
        contactName: String,
        phoneNumber: String
    ): Result<FamilyMember>
    suspend fun updateFamilyMember(
        id: String,
        relationship: String,
        contactName: String,
        phoneNumber: String
    ): Result<FamilyMember>
    suspend fun deleteFamilyMember(id: String): Result<Unit>
}
