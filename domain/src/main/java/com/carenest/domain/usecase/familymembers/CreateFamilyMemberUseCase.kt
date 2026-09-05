package com.carenest.domain.usecase.familymembers

import com.carenest.domain.model.familymembers.FamilyMember
import com.carenest.domain.model.familymembers.FamilyMemberInput
import com.carenest.domain.repository.FamilyMembersRepository
import javax.inject.Inject

class CreateFamilyMemberUseCase @Inject constructor(
    private val familyMembersRepository: FamilyMembersRepository
) {
    suspend operator fun invoke(input: FamilyMemberInput): Result<FamilyMember> {
        return familyMembersRepository.createFamilyMember(input)
    }
}
