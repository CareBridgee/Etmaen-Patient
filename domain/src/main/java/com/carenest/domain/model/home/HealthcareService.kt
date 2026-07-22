package com.carenest.domain.model.home

data class HealthcareService(
    val id: String,
    val name: String,
    val iconResName: String = "",
    val category: ServiceCategory
)
