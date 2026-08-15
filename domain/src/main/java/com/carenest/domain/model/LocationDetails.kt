package com.carenest.domain.model

data class LocationDetails(
    val address: String,
    val apartment: String,
    val district: String,
    val city: String,
    val latitude: Double,
    val longitude: Double,
    val street: String = "",
    val building: String = "",
    val area: String = "",
    val landmark: String = "",
    val country: String = ""
)