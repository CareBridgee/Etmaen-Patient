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
    @SerialName("proposedTime") val proposedTime: ProposedTimeDto,
    @SerialName("message") val message: String?,
    @SerialName("status") val status: String,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("updatedAt") val updatedAt: String
)

@Serializable
data class NurseDto(
    @SerialName("id") val id: String,
    @SerialName("firstName") val firstName: String,
    @SerialName("lastName") val lastName: String,
    @SerialName("ratingAvg") val ratingAvg: Double,
    @SerialName("totalReviews") val totalReviews: Int
)

@Serializable
data class ProposedTimeDto(
    @SerialName("hour") val hour: Int,
    @SerialName("minute") val minute: Int,
    @SerialName("second") val second: Int,
    @SerialName("nano") val nano: Int
)
