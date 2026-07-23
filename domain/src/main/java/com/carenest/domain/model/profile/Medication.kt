package com.carenest.domain.model.profile

/**
 * UI-only medication entry used until the backend supports manual medication names.
 * It is intentionally not persisted and has no backend identifier or sync state.
 */
data class MedicationInput(
    val id: String,
    val name: String = ""
)
