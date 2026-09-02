package com.carenest.domain.usecase.familymembers

import com.carenest.domain.repository.FamilyMembersRepository
import javax.inject.Inject

class DeleteFamilyMemberUseCase @Inject constructor(
    private val familyMembersRepository: FamilyMembersRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> {
        return familyMembersRepository.deleteFamilyMember(id)
    }
}
