package com.carenest.domain.model.history

data class ServiceHistory(
    val serviceRequestId: String,
    val serviceTypeId: String,
    val serviceName: String,
    val serviceDescription: String,
    val preferredDate: String,
    val preferredTime: PreferredTime,
    val status: String,
    val nurseId: String?,
    val nurseName: String?,
    val nurseImage: String? = null,
    val createdAt: String,
    val updatedAt: String,
    val nurseProfileImageUrl: String? = null
)

data class PreferredTime(
    val hour: Int,
    val minute: Int,
    val second: Int = 0,
    val nano: Int = 0
)
