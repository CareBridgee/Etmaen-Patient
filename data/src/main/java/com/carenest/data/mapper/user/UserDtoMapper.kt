package com.carenest.data.mapper.user

import com.carenest.data.source.remote.dto.user.UserResponseDto
import com.carenest.domain.model.home.User

internal fun UserResponseDto.toDomain(): User = User(
    id = requireNotNull(id?.takeIf(String::isNotBlank)) { "Current user id is missing" },
    phoneNumber = phoneNumber.orEmpty(),
    email = email,
    firstName = firstName,
    lastName = lastName,
    dateOfBirth = dateOfBirth,
    gender = gender,
    profileImageUrl = profileImageUrl,
    isDeleted = isDeleted,
    createdAt = createdAt,
    updatedAt = updatedAt,
    lastLoginAt = lastLoginAt,
    defaultProfileId = defaultProfileId
)
