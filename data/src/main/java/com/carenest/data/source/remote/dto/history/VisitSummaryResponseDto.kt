package com.carenest.data.source.remote.dto.history

import com.carenest.data.source.remote.dto.tracking.NurseOfferDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VisitSummaryResponseDto(
    @SerialName("serviceRequestId") val serviceRequestId: String,
    @SerialName("serviceType") val serviceType: ServiceTypeDto,
    @SerialName("profile") val profile: ProfileDto,
    @SerialName("nurse") val nurse: NurseDto? = null,
    @SerialName("serviceDescription") val serviceDescription: String? = null,
    @SerialName("preferredDate") val preferredDate: String,
    @SerialName("preferredTime") val preferredTime: String,
    @SerialName("durationMinutes") val durationMinutes: Int? = null,
    @SerialName("status") val status: String,
    @SerialName("latitude") val latitude: Double,
    @SerialName("longitude") val longitude: Double,
    @SerialName("distanceKm") val distanceKm: Double? = null,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("updatedAt") val updatedAt: String,
    @SerialName("offers") val offers: List<NurseOfferDto>? = null
)

@Serializable
data class ServiceTypeDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("basePrice") val basePrice: Double,
    @SerialName("estimatedDurationMinutes") val estimatedDurationMinutes: Int,
    @SerialName("imageUrl") val imageUrl: String? = null
)

@Serializable
data class ProfileDto(
    @SerialName("id") val id: String,
    @SerialName("firstName") val firstName: String,
    @SerialName("lastName") val lastName: String,
    @SerialName("phoneNumber") val phoneNumber: String,
    @SerialName("profileImageUrl") val profileImageUrl: String? = null
)

@Serializable
data class NurseDto(
    @SerialName("id") val id: String,
    @SerialName("firstName") val firstName: String,
    @SerialName("lastName") val lastName: String,
    @SerialName("phoneNumber") val phoneNumber: String? = null,
    @SerialName("profileImageUrl") val profileImageUrl: String? = null,
    @SerialName("ratingAvg") val ratingAvg: Double,
    @SerialName("totalReviews") val totalReviews: Int,
    @SerialName("isOnline") val isOnline: Boolean = false
)
