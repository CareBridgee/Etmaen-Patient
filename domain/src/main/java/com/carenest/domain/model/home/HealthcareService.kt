package com.carenest.domain.model.home

data class HealthcareService(
    val id: String,
    val name: String,
    val estimatedDurationMinutes: Long,
    val basePrice: Double,
    val description: String,
    val iconResName: String = "",
)
