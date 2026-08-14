package com.carenest.data.source.remote.dto.history

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServiceHistoryDto(
    @SerialName("serviceRequestId") val serviceRequestId: String? = null,
    @SerialName("serviceTypeId") val serviceTypeId: String? = null,
    @SerialName("serviceName") val serviceName: String? = null,
    @SerialName("serviceDescription") val serviceDescription: String? = null,
    @SerialName("preferredDate") val preferredDate: String? = null,
    @SerialName("preferredTime") val preferredTime: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("nurseId") val nurseId: String? = null,
    @SerialName("nurseName") val nurseName: String? = null,
    @SerialName("nurseProfileImageUrl") val nurseProfileImageUrl: String? = null,
    @SerialName("createdAt") val createdAt: String? = null,
    @SerialName("updatedAt") val updatedAt: String? = null
)

@Serializable
data class PreferredTimeDto(
    @SerialName("hour") val hour: Int? = null,
    @SerialName("minute") val minute: Int? = null,
    @SerialName("second") val second: Int? = null,
    @SerialName("nano") val nano: Int? = null
)
