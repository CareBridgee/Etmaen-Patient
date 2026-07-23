package com.carenest.domain.model.profile

data class MedicalCondition(
    val localKey: String,
    val backendId: String,
    val name: String,
    val description: String?
)

data class ProfileMedicalCondition(
    val medicalConditionId: String,
    val conditionName: String
)

data class MedicalConditionsData(
    val catalog: List<MedicalCondition>,
    val saved: List<ProfileMedicalCondition>
)
