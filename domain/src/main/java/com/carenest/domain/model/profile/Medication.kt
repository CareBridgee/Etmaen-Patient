package com.carenest.domain.model.profile


data class MedicationInput(
    val uiKey: Long,
    val name: String = ""
)

data class ProfileMedication(
    val id: String,
    val profileId: String,
    val medicationId: String?,
    val name: String
)
