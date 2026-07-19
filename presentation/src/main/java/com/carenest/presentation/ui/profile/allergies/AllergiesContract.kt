package com.carenest.presentation.ui.profile.allergies

data class AllergiesState(
    val hasNoKnownAllergies: Boolean = false,
    val selectedDrugAllergies: Set<String> = emptySet(),
    val selectedFoodAllergies: Set<String> = emptySet(),
    val otherAllergies: String = ""
)

sealed interface AllergiesIntent {
    data object NoKnownAllergiesToggled : AllergiesIntent
    data class DrugAllergyToggled(val allergy: String) : AllergiesIntent
    data class FoodAllergyToggled(val allergy: String) : AllergiesIntent
    data class OtherAllergiesChanged(val allergies: String) : AllergiesIntent
    data object BackClicked : AllergiesIntent
    data object ContinueClicked : AllergiesIntent
}

sealed interface AllergiesEffect {
    data object NavigateBack : AllergiesEffect
    data object ContinueToRemainingProfile : AllergiesEffect
}
