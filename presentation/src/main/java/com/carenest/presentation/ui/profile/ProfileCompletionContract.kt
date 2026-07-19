package com.carenest.presentation.ui.profile

enum class ProfileStep {
    Welcome,
    BasicHealthInfo,
    MedicalConditions,
    Allergies,
    CurrentMedications,
    MedicalHistory,
    MobilityStatus,
    EmergencyContact,
    FinalStep
}

enum class MobilityStatus {
    Independent,
    NeedsAssistance,
    UsesWalkingAid,
    WheelchairUser,
    Bedridden
}

enum class EmergencyRelationship {
    Spouse,
    Parent,
    Sibling,
    AdultChild,
    FriendOrNeighbor,
    Other
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
    val otherAllergies: String = "",
    val hasNoCurrentMedications: Boolean = false,
    val currentMedications: List<String> = listOf(""),
    val previousSurgeries: String = "",
    val previousHospitalizations: String = "",
    val mobilityStatus: MobilityStatus? = null,
    val mobilityNotes: String = "",
    val emergencyContactName: String = "",
    val emergencyRelationship: EmergencyRelationship? = null,
    val emergencyPhoneNumber: String = ""
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
    data object NoCurrentMedicationsToggled : ProfileCompletionIntent
    data object MedicationAdded : ProfileCompletionIntent
    data class MedicationChanged(val index: Int, val medication: String) : ProfileCompletionIntent
    data class MedicationRemoved(val index: Int) : ProfileCompletionIntent
    data class PreviousSurgeriesChanged(val surgeries: String) : ProfileCompletionIntent
    data class PreviousHospitalizationsChanged(val hospitalizations: String) : ProfileCompletionIntent
    data class MobilityStatusSelected(val status: MobilityStatus) : ProfileCompletionIntent
    data class MobilityNotesChanged(val notes: String) : ProfileCompletionIntent
    data class EmergencyContactNameChanged(val name: String) : ProfileCompletionIntent
    data class EmergencyRelationshipSelected(
        val relationship: EmergencyRelationship
    ) : ProfileCompletionIntent
    data class EmergencyPhoneNumberChanged(val phoneNumber: String) : ProfileCompletionIntent
    data object BackClicked : ProfileCompletionIntent
    data object ContinueClicked : ProfileCompletionIntent
    data object SkipClicked : ProfileCompletionIntent
}

sealed interface ProfileCompletionEffect {
    data object NavigateBack : ProfileCompletionEffect
    data object NavigateToHome : ProfileCompletionEffect
}
