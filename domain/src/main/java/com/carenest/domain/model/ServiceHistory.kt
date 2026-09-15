package com.carenest.domain.model

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
