package com.carenest.data.source.remote.service

import com.carenest.data.source.remote.dto.profile.ProfileRequestDto
import com.carenest.data.source.remote.dto.profile.ProfileResponseDto

interface FamilyMembersApiService {
    suspend fun getFamilyMembers(): Result<List<ProfileResponseDto>>
    suspend fun getFamilyMemberById(id: String): Result<ProfileResponseDto>
    suspend fun createFamilyMember(request: ProfileRequestDto): Result<ProfileResponseDto>
    suspend fun updateFamilyMember(id: String, request: ProfileRequestDto): Result<ProfileResponseDto>
    suspend fun deleteFamilyMember(id: String): Result<Unit>
}
