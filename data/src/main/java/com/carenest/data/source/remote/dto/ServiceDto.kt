package com.carenest.data.source.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServiceDto(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("description")
    val description: String,
    @SerialName("imageUrl")
    val imageUrl: String?,
    @SerialName("category")
    val category: String,
    @SerialName("minimumDurationMinutes")
    val minimumDurationMinutes: Int,
    @SerialName("estimatedDurationMinutes")
    val estimatedDurationMinutes: Int,
    @SerialName("basePrice")
    val basePrice: Double,
    @SerialName("includedItems")
    val includedItems: List<String>,
    @SerialName("preparationNote")
    val preparationNote: String,
    @SerialName("createdAt")
    val createdAt: String,
)