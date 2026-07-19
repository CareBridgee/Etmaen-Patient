package com.carenest.presentation.ui.profile

enum class ProfileStep {
    Welcome,
    BasicHealthInfo,
    MedicalConditions,
    Allergies
}

data class ProfileCompletionState(
    val currentStep: ProfileStep = ProfileStep.Welcome,
    val height: String = "170",
    val weight: String = "65",
    val bloodType: String = "",
    val selectedConditions: Set<String> = emptySet(),
    val otherConditions: String = "",
    val hasNoKnownAllergies: Boolean = false,
    val selectedDrugAllergies: Set<String> = emptySet(),
    val selectedFoodAllergies: Set<String> = emptySet(),
    val otherAllergies: String = ""
)

sealed interface ProfileCompletionIntent {
    data class HeightChanged(val height: String) : ProfileCompletionIntent
    data class WeightChanged(val weight: String) : ProfileCompletionIntent
    data class BloodTypeChanged(val bloodType: String) : ProfileCompletionIntent
    data class ConditionToggled(val condition: String) : ProfileCompletionIntent
    data class OtherConditionsChanged(val conditions: String) : ProfileCompletionIntent
    data object NoKnownAllergiesToggled : ProfileCompletionIntent
    data class DrugAllergyToggled(val allergy: String) : ProfileCompletionIntent
    data class FoodAllergyToggled(val allergy: String) : ProfileCompletionIntent
    data class OtherAllergiesChanged(val allergies: String) : ProfileCompletionIntent
    data object BackClicked : ProfileCompletionIntent
    data object ContinueClicked : ProfileCompletionIntent
    data object SkipClicked : ProfileCompletionIntent
}

sealed interface ProfileCompletionEffect {
    data object NavigateBack : ProfileCompletionEffect
    data object NavigateToHome : ProfileCompletionEffect
}
