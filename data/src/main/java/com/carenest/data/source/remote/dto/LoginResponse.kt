package com.carenest.data.source.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val phoneNumber: String,
    val otp: String? = null
)
