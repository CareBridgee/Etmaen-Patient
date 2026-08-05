package com.carenest.data.mapper.user

import com.carenest.data.source.local.database.entity.UserEntity
import com.carenest.domain.model.home.User

internal fun UserEntity.toDomain(): User = User(
    id = id,
    phoneNumber = phoneNumber,
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
