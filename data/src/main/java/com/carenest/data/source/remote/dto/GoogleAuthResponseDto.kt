package com.carenest.data.source.remote.dto

import com.carenest.data.source.remote.dto.user.UserResponseDto
import kotlinx.serialization.Serializable

@Serializable
data class GoogleAuthResponseDto(
    val status: String,
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val expiresIn: Long? = null,
    val user: UserResponseDto? = null,
    val pendingToken: String? = null,
    val email: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val profileImageUrl: String? = null
)
