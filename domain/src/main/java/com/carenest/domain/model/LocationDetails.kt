package com.carenest.domain.model

data class LocationDetails(
    val address: String,
    val apartment: String,
    val district: String,
    val city: String,
    val latitude: Double,
    val longitude: Double
)
