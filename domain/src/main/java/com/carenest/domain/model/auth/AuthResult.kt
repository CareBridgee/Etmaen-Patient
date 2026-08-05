package com.carenest.domain.model.auth

import com.carenest.domain.model.home.User

data class AuthResult(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long,
    val user: User
)
