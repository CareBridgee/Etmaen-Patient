package com.carenest.data.mapper.family_members

import com.carenest.data.source.remote.dto.family_members.FamilyMemberResponseDto
import com.carenest.domain.model.family_members.FamilyMember

internal fun FamilyMemberResponseDto.toDomain(): FamilyMember = FamilyMember(
    id = id.orEmpty(),
    profileId = profileId,
    contactName = contactName.orEmpty(),
    relationship = relationship,
    phoneNumber = phoneNumber.orEmpty(),
    createdAt = createdAt,
    updatedAt = updatedAt
)
