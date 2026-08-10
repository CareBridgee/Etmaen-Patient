package com.carenest.data.source.remote.dto.tracking

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServiceRequestTrackingDto(
    @SerialName("serviceRequestId") val serviceRequestId: String,
    @SerialName("serviceType") val serviceType: ServiceTypeDto,
    @SerialName("nurse") val nurse: NurseDto? = null,
    @SerialName("serviceDescription") val serviceDescription: String? = null,
    @SerialName("preferredDate") val preferredDate: String,
    @SerialName("preferredTime") val preferredTime: String,
    @SerialName("status") val status: String,
    @SerialName("distanceKm") val distanceKm: Double? = null,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("updatedAt") val updatedAt: String
)

@Serializable
data class ServiceTypeDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("basePrice") val basePrice: Double,
    @SerialName("estimatedDurationMinutes") val estimatedDurationMinutes: Int
)
