package com.carenest.data.mapper

import com.carenest.data.source.remote.dto.tracking.NurseDetailsDto
import com.carenest.data.source.remote.dto.tracking.ServiceRequestTrackingDto
import com.carenest.data.utils.parseTimeString
import com.carenest.domain.model.NurseTrackingInfo
import java.util.Locale

fun ServiceRequestTrackingDto.toDomain(nurseDetails: NurseDetailsDto?): NurseTrackingInfo {
    val (hour, minute) = parseTimeString(preferredTime)
    val amPm = if (hour >= 12) "PM" else "AM"
    val displayHour = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    val arrivalTime = String.format(Locale.US, "%02d:%02d %s", displayHour, minute, amPm)
    
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
        requestId = serviceRequestId,
        status = status,
        isOnline = true // Force to true as per requirement
    )
}
