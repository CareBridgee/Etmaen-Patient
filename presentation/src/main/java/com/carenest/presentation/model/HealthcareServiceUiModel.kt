package com.carenest.presentation.model

import com.carenest.domain.model.home.HealthcareService
import kotlinx.serialization.Serializable


@Serializable
data class HealthcareServiceUiModel(
    val id: String,
    val name: String,
    val estimatedDurationMinutes: Long,
    val basePrice: Double,
    val description: String,
    val iconResName: String = "",
)

fun HealthcareService.toUiModel(): HealthcareServiceUiModel {
    return HealthcareServiceUiModel(
        id = id,
        name = name,
        estimatedDurationMinutes = estimatedDurationMinutes,
        basePrice = basePrice,
        description = description,
        iconResName = iconResName?:""
    )
}

fun HealthcareServiceUiModel.toDomainModel(): HealthcareService {
    return HealthcareService(
        id = id,
        name = name,
        estimatedDurationMinutes = estimatedDurationMinutes,
        basePrice = basePrice,
        description = description,
        iconResName = iconResName
    )
}
