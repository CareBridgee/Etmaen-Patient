package com.carenest.domain.usecase.family_members

import com.carenest.domain.model.family_members.FamilyMember
import com.carenest.domain.model.family_members.FamilyMemberInput
import com.carenest.domain.repository.FamilyMembersRepository
import javax.inject.Inject

class UpdateFamilyMemberUseCase @Inject constructor(
    private val familyMembersRepository: FamilyMembersRepository
) {
    suspend operator fun invoke(id: String, input: FamilyMemberInput): Result<FamilyMember> {
        return familyMembersRepository.updateFamilyMember(id, input)
    }
}
