package com.carenest.domain.repository

import com.carenest.domain.model.family_members.FamilyMember
import com.carenest.domain.model.family_members.FamilyMemberInput

interface FamilyMembersRepository {
    suspend fun getFamilyMembers(): Result<List<FamilyMember>>
    suspend fun getFamilyMemberById(id: String): Result<FamilyMember>
    suspend fun createFamilyMember(input: FamilyMemberInput): Result<FamilyMember>
    suspend fun updateFamilyMember(id: String, input: FamilyMemberInput): Result<FamilyMember>
    suspend fun deleteFamilyMember(id: String): Result<Unit>
}
