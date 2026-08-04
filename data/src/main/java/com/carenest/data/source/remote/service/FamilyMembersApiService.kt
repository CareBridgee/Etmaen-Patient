package com.carenest.data.source.remote.service

import com.carenest.data.source.remote.dto.family_members.FamilyMemberRequestDto
import com.carenest.data.source.remote.dto.family_members.FamilyMemberResponseDto

interface FamilyMembersApiService {
    suspend fun getFamilyMembers(profileId: String? = null): Result<List<FamilyMemberResponseDto>>
    suspend fun getFamilyMemberById(id: String): Result<FamilyMemberResponseDto>
    suspend fun createFamilyMember(profileId: String? = null, request: FamilyMemberRequestDto): Result<FamilyMemberResponseDto>
    suspend fun updateFamilyMember(id: String, request: FamilyMemberRequestDto): Result<FamilyMemberResponseDto>
    suspend fun deleteFamilyMember(id: String): Result<Unit>
}
