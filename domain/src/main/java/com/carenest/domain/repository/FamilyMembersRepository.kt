package com.carenest.domain.repository

import com.carenest.domain.model.familymembers.FamilyMember
import com.carenest.domain.model.familymembers.FamilyMemberInput

interface FamilyMembersRepository {
    suspend fun getFamilyMembers(): Result<List<FamilyMember>>
    suspend fun getFamilyMemberById(id: String): Result<FamilyMember>
    suspend fun createFamilyMember(input: FamilyMemberInput): Result<FamilyMember>
    suspend fun updateFamilyMember(id: String, input: FamilyMemberInput): Result<FamilyMember>
    suspend fun deleteFamilyMember(id: String): Result<Unit>
}
