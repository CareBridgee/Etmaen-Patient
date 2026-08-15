package com.carenest.data.source.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateServiceRequestDto(
    @SerialName("profileId") val profileId: String,
    @SerialName("serviceTypeId") val serviceTypeId: String,
    @SerialName("latitude") val latitude: Double,
    @SerialName("longitude") val longitude: Double,
    @SerialName("address") val address: String,
    @SerialName("district") val district: String,
    @SerialName("apartment") val apartment: String,
    @SerialName("preferredDate") val preferredDate: String, // "yyyy-MM-dd"
    @SerialName("preferredTime") val preferredTime: String, // "HH:mm:ss"
    @SerialName("serviceDescription") val serviceDescription: String,
)

@Serializable
data class NearbyNurseDto(
    @SerialName("nurseId") val nurseId: String,
    @SerialName("latitude") val latitude: Double,
    @SerialName("longitude") val longitude: Double,
    @SerialName("distanceKm") val distanceKm: Double,
)

@Serializable
data class ServiceRequestResponseDto(
    @SerialName("serviceRequestId") val serviceRequestId: String,
    @SerialName("profileId") val profileId: String,
    @SerialName("serviceTypeId") val serviceTypeId: String,
    @SerialName("status") val status: String,
    @SerialName("latitude") val latitude: Double,
    @SerialName("longitude") val longitude: Double,
    @SerialName("nearbyNurses") val nearbyNurses: List<NearbyNurseDto> = emptyList(),
    @SerialName("createdAt") val createdAt: String? = null,
)

