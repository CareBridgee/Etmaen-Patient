package com.carenest.data.repository

import com.carenest.data.mapper.family_members.toDomain
import com.carenest.data.source.remote.datasource.FamilyMembersDataSource
import com.carenest.data.source.remote.dto.family_members.FamilyMemberRequestDto
import com.carenest.domain.model.family_members.FamilyMember
import com.carenest.domain.repository.FamilyMembersRepository
import com.carenest.domain.usecase.profile.GetDefaultProfileUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FamilyMembersRepositoryImpl @Inject constructor(
    private val dataSource: FamilyMembersDataSource,
    private val getDefaultProfileUseCase: GetDefaultProfileUseCase
) : FamilyMembersRepository {

    private suspend fun fetchDefaultProfileId(): String? {
        return getDefaultProfileUseCase().getOrNull()?.id
    }

    override suspend fun getFamilyMembers(): Result<List<FamilyMember>> {
        val profileId = fetchDefaultProfileId()
        return dataSource.getFamilyMembers(profileId).mapCatching { list -> list.map { it.toDomain() } }.profileFailure()
    }

    override suspend fun getFamilyMemberById(id: String): Result<FamilyMember> =
        dataSource.getFamilyMemberById(id).mapCatching { it.toDomain() }.profileFailure()

    override suspend fun createFamilyMember(
        relationship: String,
        contactName: String,
        phoneNumber: String
    ): Result<FamilyMember> {
        val profileId = fetchDefaultProfileId()
        val request = FamilyMemberRequestDto(
            contactName = contactName,
            relationship = relationship,
            phoneNumber = phoneNumber
        )
        return dataSource.createFamilyMember(profileId, request).mapCatching { it.toDomain() }.profileFailure()
    }

    override suspend fun updateFamilyMember(
        id: String,
        relationship: String,
        contactName: String,
        phoneNumber: String
    ): Result<FamilyMember> {
        val request = FamilyMemberRequestDto(
            contactName = contactName,
            relationship = relationship,
            phoneNumber = phoneNumber
        )
        return dataSource.updateFamilyMember(id, request).mapCatching { it.toDomain() }.profileFailure()
    }

    override suspend fun deleteFamilyMember(id: String): Result<Unit> =
        dataSource.deleteFamilyMember(id).profileFailure()
}
