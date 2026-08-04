package com.carenest.domain.usecase.family_members

import com.carenest.domain.model.family_members.FamilyMember
import com.carenest.domain.repository.FamilyMembersRepository
import javax.inject.Inject

class CreateFamilyMemberUseCase @Inject constructor(
    private val familyMembersRepository: FamilyMembersRepository
) {
    suspend operator fun invoke(
        relationship: String,
        contactName: String,
        phoneNumber: String
    ): Result<FamilyMember> {
        return familyMembersRepository.createFamilyMember(
            relationship = relationship.trim(),
            contactName = contactName.trim(),
            phoneNumber = phoneNumber.trim()
        )
    }
}
