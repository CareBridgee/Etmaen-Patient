package com.carenest.data.source.remote.dto.profile

import kotlinx.serialization.Serializable

@Serializable
data class MedicalConditionDto(
    val id: String? = null,
    val name: String? = null,
    val description: String? = null
)

@Serializable
data class AllergyDto(
    val id: String? = null,
    val name: String? = null,
    val type: String? = null
)

@Serializable
data class MedicationDto(
    val id: String? = null,
    val name: String? = null
)
