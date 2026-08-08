package com.carenest.domain.model.visit_summary


data class VisitSummary(
    val requestId: String,
    val professionalName: String,
    val professionalImageUrl: String? = null,
    val serviceType: String,
    val serviceIconUrl: String? = null,
    val durationMinutes: Int,
    val completedDate: String,
    val totalAmount: Double,
    val isVerified: Boolean,
)