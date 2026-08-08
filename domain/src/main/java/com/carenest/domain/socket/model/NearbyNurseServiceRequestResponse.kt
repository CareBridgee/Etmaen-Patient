package com.carenest.domain.socket.model

data class NearbyNurseServiceRequestResponse(
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
)
