package com.carenest.data.source.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class VerifyOtpRequest(
    val phoneNumber: String,
    val otp: String,
)
