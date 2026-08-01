package com.carenest.data.socket.models

import com.carenest.domain.socket.model.NearbyNurseServiceRequestResponse
import kotlinx.serialization.Serializable

@Serializable
data class NearbyNurseServiceRequestResponseDto(
    val serviceRequestId: String,
    val profileId: String,
    val serviceTypeId: String,
    val serviceName: String,
    val serviceDescription: String?,
    val preferredDate: String?,
    val preferredTime: String?,
    val status: String,
    val latitude: Double,
    val longitude: Double,
    val distanceKm: Double,
    val estimatedPrice: Double?,
    val createdAt: String
) {
    fun toDomain() = NearbyNurseServiceRequestResponse(
        serviceRequestId, profileId, serviceTypeId, serviceName, serviceDescription, preferredDate,
        preferredTime, status, latitude, longitude, distanceKm, estimatedPrice, createdAt
    )
}
