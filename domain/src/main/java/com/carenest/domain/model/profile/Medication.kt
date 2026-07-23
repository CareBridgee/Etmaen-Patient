package com.carenest.domain.model.profile

data class Medication(
    val localKey: String,
    val backendId: String?,
    val name: String,
    val source: CatalogSource,
    val syncState: SyncState
)

data class ProfileMedication(
    val medicationId: String,
    val medicationName: String
)

data class MedicationsData(
    val catalog: List<Medication>,
    val saved: List<ProfileMedication>
)
