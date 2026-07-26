package com.carenest.domain.model.visit_summary


data class VisitSummary(
    val requestId: String,
    val professionalName: String,
    val serviceType: String,
    val durationMinutes: Int,
    val completedDate: String,
    val totalAmount: Double,
    val isVerified: Boolean,
)