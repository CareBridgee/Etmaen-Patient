package com.carenest.domain.usecase.familymembers

import com.carenest.domain.model.familymembers.FamilyMember
import com.carenest.domain.repository.FamilyMembersRepository
import javax.inject.Inject

class GetFamilyMembersUseCase @Inject constructor(
    private val familyMembersRepository: FamilyMembersRepository
) {
    suspend operator fun invoke(): Result<List<FamilyMember>> {
        return familyMembersRepository.getFamilyMembers()
    }
}
