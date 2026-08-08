package com.carenest.data.mapper.tracking

import com.carenest.data.source.remote.dto.tracking.NurseDetailsDto
import com.carenest.data.source.remote.dto.tracking.ServiceRequestTrackingDto
import com.carenest.domain.model.tracking.NurseTrackingInfo

fun ServiceRequestTrackingDto.toDomain(nurseDetails: NurseDetailsDto?): NurseTrackingInfo {
    val arrivalTime = "${preferredTime.hour}:${preferredTime.minute.toString().padStart(2, '0')}"
    
    return NurseTrackingInfo(
        nurseId = nurse?.id.orEmpty(),
        name = nurse?.let { "${it.firstName} ${it.lastName}" }.orEmpty(),
        photoUrl = nurseDetails?.profileImageUrl ?: nurse?.profileImageUrl,
        rating = nurseDetails?.ratingAvg ?: nurse?.ratingAvg ?: 0.0,
        reviewsCount = nurseDetails?.totalReviews ?: nurse?.totalReviews ?: 0,
        estimatedArrivalTime = arrivalTime,
        distanceKm = distanceKm ?: 0.0,
        specialty = serviceType.name,
        phoneNumber = nurseDetails?.phoneNumber.orEmpty(),
        cancellationWindowMinutes = 2, // Mock or provided by backend
        requestId = serviceRequestId
    )
}
