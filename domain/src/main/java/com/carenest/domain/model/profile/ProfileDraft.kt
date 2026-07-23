package com.carenest.domain.model.profile

data class LocalMedicationEntry(
    val localId: String,
    val name: String = ""
)

data class ProfileLocalDraft(
    val otherConditions: String = "",
    val otherAllergies: String = "",
    val noKnownAllergiesConfirmed: Boolean = false,
    val medications: List<LocalMedicationEntry> = emptyList(),
    val noCurrentMedicationsConfirmed: Boolean = false,
    val pendingMobilityStatus: String? = null,
    val pendingMobilityNotes: String? = null
)
