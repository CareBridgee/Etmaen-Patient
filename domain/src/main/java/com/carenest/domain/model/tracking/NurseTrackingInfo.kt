package com.carenest.domain.model.tracking


data class NurseTrackingInfo(
    val nurseId: String,
    val name: String,
    val photoUrl: String?,
    val rating: Double,
    val reviewsCount: Int,
    val estimatedArrivalTime: String,
    val distanceKm: Double,
    val specialty: String,
    val phoneNumber: String,
    val cancellationWindowMinutes: Int,
    val requestId: String,
    val status: String,
)
