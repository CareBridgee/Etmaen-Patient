package com.carenest.domain.model.profile

enum class AllergyType { DRUG, FOOD, OTHER }

data class Allergy(
    val id: String,
    val name: String,
    val type: AllergyType
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
