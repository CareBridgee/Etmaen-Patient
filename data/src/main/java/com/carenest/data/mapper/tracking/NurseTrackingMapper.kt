package com.carenest.data.mapper.tracking

import com.carenest.data.source.remote.dto.tracking.NurseOfferDto
import com.carenest.domain.model.tracking.NurseTrackingInfo

fun NurseOfferDto.toDomain(): NurseTrackingInfo {
    val arrivalTime = "${proposedTime.hour}:${proposedTime.minute.toString().padStart(2, '0')}"
    
    return NurseTrackingInfo(
        nurseId = nurse.id,
        name = "${nurse.firstName} ${nurse.lastName}",
        photoUrl = null, // Not available in the offer DTO
        rating = nurse.ratingAvg,
        reviewsCount = nurse.totalReviews,
        estimatedArrivalTime = arrivalTime,
        distanceKm = 0.0, // Should be calculated or provided by backend
        specialty = "Registered Nurse", // Should be part of the DTO
        phoneNumber = "", // Should be part of the DTO
        cancellationWindowMinutes = 2, // Mock or provided by backend
        requestId = serviceRequestId
    )
}
