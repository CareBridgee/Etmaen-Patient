package com.carenest.data.source.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class TokenPairResponse(
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val expiresIn: Long? = null
)
