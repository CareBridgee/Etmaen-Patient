package com.carenest.domain.model.profile

enum class AllergyType { DRUG, FOOD, OTHER }

data class Allergy(
    val localKey: String,
    val backendId: String?,
    val name: String,
    val type: AllergyType,
    val source: CatalogSource,
    val syncState: SyncState
)

data class ProfileAllergy(
    val allergyId: String,
    val allergyName: String,
    val type: AllergyType
)

data class AllergiesData(
    val catalog: List<Allergy>,
    val saved: List<ProfileAllergy>
)
