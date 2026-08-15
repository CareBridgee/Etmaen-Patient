package com.carenest.data.mapper.user

import com.carenest.data.source.local.database.entity.UserEntity
import com.carenest.data.source.remote.dto.user.UpdateUserRequestDto
import com.carenest.domain.model.home.User
import com.carenest.domain.model.user.UserUpdate

internal fun User.toEntity(): UserEntity = UserEntity(
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
    defaultProfileId = defaultProfileId,
    credit = credit,
)

internal fun UserUpdate.toDto(): UpdateUserRequestDto = UpdateUserRequestDto(
    firstName = firstName.trim(),
    lastName = lastName.trim(),
    email = email?.takeIf(String::isNotBlank),
    dateOfBirth = dateOfBirth?.takeIf(String::isNotBlank),
    gender = gender?.uppercase()?.takeIf(String::isNotBlank),
    profileImageUrl = profileImageUrl?.takeIf(String::isNotBlank)
)
