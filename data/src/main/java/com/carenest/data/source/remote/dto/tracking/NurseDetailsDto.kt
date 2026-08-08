package com.carenest.data.source.remote.dto.tracking

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NurseDetailsDto(
    @SerialName("id") val id: String,
    @SerialName("phoneNumber") val phoneNumber: String,
    @SerialName("profileImageUrl") val profileImageUrl: String? = null,
    @SerialName("specialization") val specialization: String? = null,
    @SerialName("ratingAvg") val ratingAvg: Double,
    @SerialName("totalReviews") val totalReviews: Int
)
