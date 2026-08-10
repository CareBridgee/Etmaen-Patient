package com.carenest.data.mapper

import com.carenest.data.source.remote.dto.ServiceDto
import com.carenest.domain.model.ServiceDetailsModel
import com.carenest.domain.model.home.HealthcareService

internal fun ServiceDto.toDomain() : HealthcareService = HealthcareService(
    id = id,
    name = name,
    estimatedDurationMinutes = estimatedDurationMinutes.toLong(),
    basePrice = basePrice,
    description = description.orEmpty(),
    iconResName = imageUrl,
)


internal fun ServiceDto.toServiceDetails() : ServiceDetailsModel = ServiceDetailsModel(
    id = id,
    name = name,
    description = description.orEmpty(),
    imageUrl = imageUrl?:"",
    category = category,
    minimumDurationMinutes = minimumDurationMinutes,
    estimatedDurationMinutes = estimatedDurationMinutes,
    basePrice = basePrice,
    includedItems = includedItems,
    preparationNote = preparationNote.orEmpty(),
    createdAt = createdAt,
)