package com.carenest.data.socket.models

import com.carenest.domain.socket.model.NearbyNurseServiceRequestResponse
import kotlinx.serialization.Serializable

@Serializable
data class NearbyNurseServiceRequestResponseDto(
    val serviceRequestId: String? = null,
    val profileId: String? = null,
    val serviceTypeId: String? = null,
    val serviceName: String? = null,
    val serviceDescription: String? = null,
    val preferredDate: String? = null,
    val preferredTime: String? = null,
    val status: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val distanceKm: Double? = null,
    val estimatedPrice: Double? = null,
    val createdAt: String? = null
) {
    fun toDomain() = NearbyNurseServiceRequestResponse(
        serviceRequestId ?: "",
        profileId ?: "",
        serviceTypeId ?: "",
        serviceName ?: "",
        serviceDescription,
        preferredDate,
        preferredTime,
        status ?: "SEARCHING",
        latitude ?: 0.0,
        longitude ?: 0.0,
        distanceKm ?: 0.0,
        estimatedPrice,
        createdAt ?: ""
    )
}
