package com.carenest.data.source.remote.dto.tracking

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VisitCodeResponseDto(
    @SerialName("serviceRequestId") val serviceRequestId: String,
    @SerialName("code") val code: String,
    @SerialName("expiresAt") val expiresAt: String
)
