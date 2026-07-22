package com.carenest.domain.model.home

data class Booking(
    val id: String,
    val providerName: String,
    val serviceName: String,
    val timeText: String,
    val statusText: String,
    val avatarUrl: String? = null
)
