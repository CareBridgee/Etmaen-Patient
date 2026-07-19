package com.carenest.presentation.ui.profile.medicalconditions

data class MedicalConditionsState(
    val selectedConditions: Set<String> = emptySet(),
    val otherConditions: String = ""
)

sealed interface MedicalConditionsIntent {
    data class ConditionToggled(val condition: String) : MedicalConditionsIntent
    data class OtherConditionsChanged(val conditions: String) : MedicalConditionsIntent
    data object BackClicked : MedicalConditionsIntent
    data object ContinueClicked : MedicalConditionsIntent
}

sealed interface MedicalConditionsEffect {
    data object NavigateBack : MedicalConditionsEffect
    data object NavigateToAllergies : MedicalConditionsEffect
}
