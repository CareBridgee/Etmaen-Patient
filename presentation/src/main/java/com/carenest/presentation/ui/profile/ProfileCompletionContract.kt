package com.carenest.presentation.ui.profile

enum class ProfileStep {
    Welcome,
    PersonalInfo,
    BasicHealthInfo,
    MedicalConditions,
    Allergies
}

data class ProfileCompletionState(
    val currentStep: ProfileStep = ProfileStep.Welcome,
    val firstName: String = "",
    val lastName: String = "",
    val dateOfBirth: String = "",
    val nationalId: String = "",
    val gender: String = "",
    val accountType: String = "",
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
    data class FirstNameChanged(val firstName: String) : ProfileCompletionIntent
    data class LastNameChanged(val lastName: String) : ProfileCompletionIntent
    data class DateOfBirthChanged(val dateOfBirth: String) : ProfileCompletionIntent
    data class NationalIdChanged(val nationalId: String) : ProfileCompletionIntent
    data class GenderChanged(val gender: String) : ProfileCompletionIntent
    data class AccountTypeChanged(val accountType: String) : ProfileCompletionIntent
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
