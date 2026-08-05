package com.carenest.domain.usecase.family_members

import com.carenest.domain.model.family_members.FamilyMember
import com.carenest.domain.repository.FamilyMembersRepository
import javax.inject.Inject

class GetFamilyMembersUseCase @Inject constructor(
    private val familyMembersRepository: FamilyMembersRepository
) {
    suspend operator fun invoke(): Result<List<FamilyMember>> {
        return familyMembersRepository.getFamilyMembers()
    }
}
