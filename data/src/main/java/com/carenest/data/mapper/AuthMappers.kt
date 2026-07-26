package com.carenest.data.mapper

import com.carenest.data.source.remote.dto.AuthResponse
import com.carenest.data.source.remote.dto.UserDto
import com.carenest.domain.model.Patient
import com.carenest.domain.model.auth.AuthResult

fun AuthResponse.toDomain(): AuthResult {
    return AuthResult(
        accessToken = accessToken,
        refreshToken = refreshToken,
        expiresIn = expiresIn,
        patient = user.toDomain()
    )
}

fun UserDto.toDomain(): Patient {
    return Patient(
        id = id,
        phoneNumber = phoneNumber,
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
}
