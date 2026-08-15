package com.carenest.domain.model

/**
 * The response returned by the backend after a patient submits a new service request.
 * Note: the API response does not return a separate reservationId.
 * The [serviceRequestId] is used both for socket topic subscription and for requesting offers.
 */
data class ServiceRequestResult(
    val serviceRequestId: String,
    val status: String,
    val nearbyNursesCount: Int,
)

/**
 * Represents the preferred time for the service.
 */
data class PreferredTime(
    val hour: Int,
    val minute: Int,
    val second: Int = 0,
    val nano: Int = 0,
)

/**
 * The input model for creating a new service request.
 */
data class CreateServiceRequestParams(
    val profileId: String,
    val serviceTypeId: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val district: String,
    val apartment: String,
    val preferredDate: String,      // "yyyy-MM-dd"
    val preferredTime: PreferredTime,
    val serviceDescription: String,
)
