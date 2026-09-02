package com.carenest.data.repository

import com.carenest.data.mapper.toDomain
import com.carenest.data.mapper.toDto
import com.carenest.data.source.remote.datasource.FamilyMembersDataSource
import com.carenest.domain.model.familymembers.FamilyMember
import com.carenest.domain.model.familymembers.FamilyMemberInput
import com.carenest.domain.repository.FamilyMembersRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FamilyMembersRepositoryImpl @Inject constructor(
    private val dataSource: FamilyMembersDataSource
) : FamilyMembersRepository {

    override suspend fun getFamilyMembers(): Result<List<FamilyMember>> =
        dataSource.getFamilyMembers()
            .mapCatching { list -> list.map { it.toDomain() }.filter { !it.isDeleted } }
            .profileFailure()

    override suspend fun getFamilyMemberById(id: String): Result<FamilyMember> =
        dataSource.getFamilyMemberById(id)
            .mapCatching { it.toDomain() }
            .profileFailure()

    override suspend fun createFamilyMember(input: FamilyMemberInput): Result<FamilyMember> =
        dataSource.createFamilyMember(input.toDto())
            .mapCatching { it.toDomain() }
            .profileFailure()

    override suspend fun updateFamilyMember(id: String, input: FamilyMemberInput): Result<FamilyMember> =
        dataSource.updateFamilyMember(id, input.toDto())
            .mapCatching { it.toDomain() }
            .profileFailure()

    override suspend fun deleteFamilyMember(id: String): Result<Unit> =
        dataSource.deleteFamilyMember(id)
            .profileFailure()
}
