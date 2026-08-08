package com.carenest.presentation.ui.profile_completion

import com.carenest.domain.model.profile.AllergyType
import com.carenest.domain.model.profile.EmergencyContact
import com.carenest.domain.model.profile.EmergencyRelationship
import com.carenest.domain.model.profile.MedicationInput
import com.carenest.domain.model.profile.MedicationValidationErrors
import com.carenest.domain.model.profile.MobilityStatus
import com.carenest.domain.model.profile.Profile
import com.carenest.domain.model.profile.ProfileField
import com.carenest.domain.model.profile.ProfileValidationError

import kotlinx.serialization.Serializable

enum class ProfileStep {
    Welcome, BasicHealthInfo, MedicalConditions, Allergies, CurrentMedications,
    MedicalHistory, MobilityStatus, EmergencyContact, FinalStep
}

@Serializable
enum class ProfileCompletionSource {
    REGISTRATION,
    FAMILY_MEMBER
}

data class ProfileCatalogOption(
    val id: String,
    val label: String
)

data class ProfileAllergyOption(
    val id: String,
    val label: String,
    val type: AllergyType
)

data class ProfileCompletionState(
    val isEditMode: Boolean = false,
    val source: ProfileCompletionSource = ProfileCompletionSource.REGISTRATION,
    val currentStep: ProfileStep = ProfileStep.Welcome,
    val profile: Profile? = null,
    val profileId: String? = null,
    val isInitializing: Boolean = true,
    val isLoadingStep: Boolean = false,
    val isSubmitting: Boolean = false,
    val initialized: Boolean = false,
    val errorMessage: String? = null,
    val validationErrors: Map<ProfileField, ProfileValidationError> = emptyMap(),
    val medicationValidationErrors: Map<Long, MedicationValidationErrors> = emptyMap(),
    val loadedSteps: Set<ProfileStep> = emptySet(),
    val height: String = "",
    val weight: String = "",
    val bloodType: String = "",
    val conditionCatalog: List<ProfileCatalogOption> = emptyList(),
    val selectedConditionIds: Set<String> = emptySet(),
    val otherConditions: String = "",
    val hasNoKnownAllergies: Boolean = false,
    val allergyCatalog: List<ProfileAllergyOption> = emptyList(),
    val selectedAllergyIds: Set<String> = emptySet(),
    val otherAllergies: String = "",
    val hasNoCurrentMedications: Boolean = false,
    val currentMedications: List<MedicationInput> = emptyList(),
    val previousSurgeries: String = "",
    val previousHospitalizations: String = "",
    val mobilityStatus: MobilityStatus? = null,
    val mobilityNotes: String = "",
    val emergencyContacts: List<EmergencyContact> = emptyList(),
    val emergencyContactsLoaded: Boolean = false,
    val emergencyContactId: String? = null,
    val emergencyContactName: String = "",
    val emergencyRelationship: EmergencyRelationship? = null,
    val emergencyPhoneNumber: String = ""
)

sealed interface ProfileCompletionIntent {
    data object ConfigureEditMode : ProfileCompletionIntent
    data class HeightChanged(val height: String) : ProfileCompletionIntent
    data class WeightChanged(val weight: String) : ProfileCompletionIntent
    data class BloodTypeChanged(val bloodType: String) : ProfileCompletionIntent
    data class ConditionToggled(val id: String) : ProfileCompletionIntent
    data class OtherConditionsChanged(val conditions: String) : ProfileCompletionIntent
    data object NoKnownAllergiesToggled : ProfileCompletionIntent
    data class AllergyToggled(val id: String) : ProfileCompletionIntent
    data class OtherAllergiesChanged(val allergies: String) : ProfileCompletionIntent
    data object NoCurrentMedicationsToggled : ProfileCompletionIntent
    data object MedicationAdded : ProfileCompletionIntent
    data class MedicationNameChanged(val index: Int, val value: String) : ProfileCompletionIntent
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
    data object RetryClicked : ProfileCompletionIntent
}

sealed interface ProfileCompletionEffect {
    data object NavigateBack : ProfileCompletionEffect
    data object NavigateToHome : ProfileCompletionEffect
    data object NavigateToFamilyMembers : ProfileCompletionEffect
    data object NavigateAfterEdit : ProfileCompletionEffect
}
