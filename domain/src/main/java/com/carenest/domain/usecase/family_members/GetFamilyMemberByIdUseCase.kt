package com.carenest.domain.usecase.family_members

import com.carenest.domain.model.family_members.FamilyMember
import com.carenest.domain.repository.FamilyMembersRepository
import javax.inject.Inject

class GetFamilyMemberByIdUseCase @Inject constructor(
    private val familyMembersRepository: FamilyMembersRepository
) {
    suspend operator fun invoke(id: String): Result<FamilyMember> {
        return familyMembersRepository.getFamilyMemberById(id)
    }
}
