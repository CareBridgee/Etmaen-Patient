package com.carenest.domain.model.profile

enum class CatalogSource { REMOTE, FALLBACK }

enum class SyncState { SYNCED, LOCAL_ONLY, PENDING, FAILED }

data class LocalMedicationEntry(
    val localId: String,
    val backendMedicationId: String? = null,
    val name: String = "",
    val dosage: String = "",
    val frequency: String = "",
    val syncState: SyncState = SyncState.LOCAL_ONLY
)

data class ProfileLocalDraft(
    val selectedConditionKeys: Set<String> = emptySet(),
    val otherConditions: String = "",
    val selectedAllergyKeys: Set<String> = emptySet(),
    val otherAllergies: String = "",
    val noKnownAllergiesConfirmed: Boolean = false,
    val medications: List<LocalMedicationEntry> = emptyList(),
    val noCurrentMedicationsConfirmed: Boolean = false,
    val pendingMobilityStatus: String? = null,
    val pendingMobilityNotes: String? = null
)
