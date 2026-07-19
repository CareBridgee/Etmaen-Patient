package com.carenest.data.source.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val phoneNumber: String
)
