package com.carenest.data.mapper.history

import com.carenest.data.source.remote.dto.history.VisitSummaryResponseDto
import com.carenest.domain.model.VisitSummary

fun VisitSummaryResponseDto.toDomain(): VisitSummary {
    return VisitSummary(
        requestId = serviceRequestId,
        professionalName = nurse?.let { "${it.firstName} ${it.lastName}" } ?: "Unassigned",
        professionalImageUrl = nurse?.profileImageUrl,
        serviceType = serviceType.name,
        serviceIconUrl = serviceType.imageUrl,
        durationMinutes = durationMinutes ?: serviceType.estimatedDurationMinutes,
        completedDate = preferredDate,
        totalAmount = serviceType.basePrice,
        isVerified = status == "COMPLETED"
    )
}
