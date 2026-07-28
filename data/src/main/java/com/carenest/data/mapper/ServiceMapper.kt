package com.carenest.data.mapper

import com.carenest.data.source.remote.dto.ServiceDto
import com.carenest.domain.model.home.HealthcareService

internal fun ServiceDto.toDomain() : HealthcareService = HealthcareService(
    id = id,
    name = name,
    estimatedDurationMinutes = estimatedDurationMinutes.toLong(),
    basePrice = basePrice,
    description = description,
    iconResName = imageUrl,
)