package com.carenest.data.source.remote.dto.tracking

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NurseOfferDto(
    @SerialName("id") val id: String,
    @SerialName("serviceRequestId") val serviceRequestId: String,
    @SerialName("nurse") val nurse: NurseDto,
    @SerialName("proposedPrice") val proposedPrice: Double,
    @SerialName("proposedDate") val proposedDate: String,
    @SerialName("proposedTime") val proposedTime: String,
    @SerialName("message") val message: String? = null,
    @SerialName("status") val status: String,
    @SerialName("distanceKm") val distanceKm: Double,
    @SerialName("serviceTypeName") val serviceTypeName: String,
    @SerialName("estimatedDurationMinutes") val estimatedDurationMinutes: Int,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("updatedAt") val updatedAt: String
)

@Serializable
data class NurseDto(
    @SerialName("id") val id: String,
    @SerialName("firstName") val firstName: String,
    @SerialName("lastName") val lastName: String,
    @SerialName("profileImageUrl") val profileImageUrl: String? = null,
    @SerialName("ratingAvg") val ratingAvg: Double,
    @SerialName("totalReviews") val totalReviews: Int,
    @SerialName("isOnline") val isOnline: Boolean = false
)
