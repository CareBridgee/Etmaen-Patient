package com.carenest.data.mapper.profile

import com.carenest.data.source.remote.dto.profile.EmergencyContactResponseDto
import com.carenest.domain.model.profile.EmergencyContact

internal fun EmergencyContactResponseDto.toDomain(): EmergencyContact = EmergencyContact(
    id = id.requiredUuid("emergency contact id"),
    profileId = profileId.optionalUuid("emergency contact profile id"),
    contactName = contactName.requiredText("emergency contact name"),
    relationship = relationship,
    phoneNumber = phoneNumber.requiredText("emergency contact phone number")
)
