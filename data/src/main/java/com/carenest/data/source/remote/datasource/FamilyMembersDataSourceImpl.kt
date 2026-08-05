package com.carenest.data.source.remote.datasource

import com.carenest.data.source.remote.dto.profile.ProfileRequestDto
import com.carenest.data.source.remote.dto.profile.ProfileResponseDto
import com.carenest.data.source.remote.service.FamilyMembersApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FamilyMembersDataSourceImpl @Inject constructor(
    private val apiService: FamilyMembersApiService
) : FamilyMembersDataSource {

    override suspend fun getFamilyMembers(): Result<List<ProfileResponseDto>> =
        apiService.getFamilyMembers()

    override suspend fun getFamilyMemberById(id: String): Result<ProfileResponseDto> =
        apiService.getFamilyMemberById(id)

    override suspend fun createFamilyMember(request: ProfileRequestDto): Result<ProfileResponseDto> =
        apiService.createFamilyMember(request)

    override suspend fun updateFamilyMember(id: String, request: ProfileRequestDto): Result<ProfileResponseDto> =
        apiService.updateFamilyMember(id, request)

    override suspend fun deleteFamilyMember(id: String): Result<Unit> =
        apiService.deleteFamilyMember(id)
}
