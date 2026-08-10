package com.carenest.data.source.remote.dto.history

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReviewRequestDto(
    @SerialName("serviceRequestId") val serviceRequestId: String,
    @SerialName("rating") val rating: Int,
    @SerialName("reviewText") val reviewText: String? = null,
    @SerialName("isAnonymous") val isAnonymous: Boolean
)
