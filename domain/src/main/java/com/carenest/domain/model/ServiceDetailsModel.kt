package com.carenest.domain.model

data class ServiceDetailsModel(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val category: String,
    val minimumDurationMinutes: Int,
    val estimatedDurationMinutes: Int,
    val basePrice: Double,
    val includedItems: List<String>,
    val preparationNote: String,
    val createdAt: String
)