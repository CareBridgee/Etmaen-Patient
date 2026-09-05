package com.carenest.domain.usecase.familymembers

import com.carenest.domain.model.familymembers.FamilyMember
import com.carenest.domain.repository.FamilyMembersRepository
import javax.inject.Inject

class GetFamilyMemberByIdUseCase @Inject constructor(
    private val familyMembersRepository: FamilyMembersRepository
) {
    suspend operator fun invoke(id: String): Result<FamilyMember> {
        return familyMembersRepository.getFamilyMemberById(id)
    }
}
