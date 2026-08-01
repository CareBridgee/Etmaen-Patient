package com.carenest.data.source.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PreferredTimeDto(
    @SerialName("hour") val hour: Int,
    @SerialName("minute") val minute: Int,
    @SerialName("second") val second: Int = 0,
    @SerialName("nano") val nano: Int = 0,
)

@Serializable
data class CreateServiceRequestDto(
    @SerialName("profileId") val profileId: String,
    @SerialName("serviceTypeId") val serviceTypeId: String,
    @SerialName("latitude") val latitude: Double,
    @SerialName("longitude") val longitude: Double,
    @SerialName("preferredDate") val preferredDate: List<Int>, // [yyyy, mm, dd]
    @SerialName("preferredTime") val preferredTime: List<Int>, // [HH, mm, ss, ns]
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
    @SerialName("createdAt") val createdAt: String,
)

