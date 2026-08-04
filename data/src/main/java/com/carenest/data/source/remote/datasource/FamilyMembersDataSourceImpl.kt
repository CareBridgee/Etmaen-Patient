package com.carenest.data.source.remote.datasource

import com.carenest.data.source.remote.dto.family_members.FamilyMemberRequestDto
import com.carenest.data.source.remote.dto.family_members.FamilyMemberResponseDto
import com.carenest.data.source.remote.service.FamilyMembersApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FamilyMembersDataSourceImpl @Inject constructor(
    private val apiService: FamilyMembersApiService
) : FamilyMembersDataSource {

    override suspend fun getFamilyMembers(profileId: String?): Result<List<FamilyMemberResponseDto>> {
        return apiService.getFamilyMembers(profileId)
    }

    override suspend fun getFamilyMemberById(id: String): Result<FamilyMemberResponseDto> {
        return apiService.getFamilyMemberById(id)
    }

    override suspend fun createFamilyMember(profileId: String?, request: FamilyMemberRequestDto): Result<FamilyMemberResponseDto> {
        return apiService.createFamilyMember(profileId, request)
    }

    override suspend fun updateFamilyMember(id: String, request: FamilyMemberRequestDto): Result<FamilyMemberResponseDto> {
        return apiService.updateFamilyMember(id, request)
    }

    override suspend fun deleteFamilyMember(id: String): Result<Unit> {
        return apiService.deleteFamilyMember(id)
    }
}
