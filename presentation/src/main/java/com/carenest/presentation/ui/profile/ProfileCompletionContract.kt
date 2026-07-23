package com.carenest.presentation.ui.profile

import com.carenest.domain.model.profile.AllergyType
import com.carenest.domain.model.profile.CatalogSource
import com.carenest.domain.model.profile.EmergencyContact
import com.carenest.domain.model.profile.LocalMedicationEntry
import com.carenest.domain.model.profile.Profile
import com.carenest.domain.model.profile.ProfileLocalDraft

enum class ProfileStep {
    Welcome, BasicHealthInfo, MedicalConditions, Allergies, CurrentMedications,
    MedicalHistory, MobilityStatus, EmergencyContact, FinalStep
}

enum class MobilityStatus { Independent, NeedsAssistance, UsesWalkingAid, WheelchairUser, Bedridden }
enum class EmergencyRelationship { Spouse, Parent, Sibling, AdultChild, FriendOrNeighbor, Other }

data class ProfileCatalogOption(
    val localKey: String,
    val label: String,
    val source: CatalogSource
)

data class ProfileAllergyOption(
    val localKey: String,
    val label: String,
    val type: AllergyType,
    val source: CatalogSource
)

data class ProfileCompletionState(
    val currentStep: ProfileStep = ProfileStep.Welcome,
    val profile: Profile? = null,
    val profileId: String? = null,
    val userKey: String? = null,
    val localDraft: ProfileLocalDraft = ProfileLocalDraft(),
    val isInitializing: Boolean = true,
    val isLoadingStep: Boolean = false,
    val isSubmitting: Boolean = false,
    val initialized: Boolean = false,
    val errorMessage: String? = null,
    val loadedSteps: Set<ProfileStep> = emptySet(),
    val height: String = "170",
    val weight: String = "65",
    val bloodType: String = "",
    val conditionCatalog: List<ProfileCatalogOption> = emptyList(),
    val selectedConditionKeys: Set<String> = emptySet(),
    val originalConditionBackendIds: Set<String> = emptySet(),
    val otherConditions: String = "",
    val hasNoKnownAllergies: Boolean = false,
    val allergyCatalog: List<ProfileAllergyOption> = emptyList(),
    val selectedAllergyKeys: Set<String> = emptySet(),
    val originalAllergyBackendIds: Set<String> = emptySet(),
    val otherAllergies: String = "",
    val hasNoCurrentMedications: Boolean = false,
    val medicationCatalog: List<ProfileCatalogOption> = emptyList(),
    val currentMedications: List<LocalMedicationEntry> = emptyList(),
    val originalMedicationBackendIds: Set<String> = emptySet(),
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
    data class HeightChanged(val height: String) : ProfileCompletionIntent
    data class WeightChanged(val weight: String) : ProfileCompletionIntent
    data class BloodTypeChanged(val bloodType: String) : ProfileCompletionIntent
    data class ConditionToggled(val localKey: String) : ProfileCompletionIntent
    data class OtherConditionsChanged(val conditions: String) : ProfileCompletionIntent
    data object NoKnownAllergiesToggled : ProfileCompletionIntent
    data class AllergyToggled(val localKey: String) : ProfileCompletionIntent
    data class OtherAllergiesChanged(val allergies: String) : ProfileCompletionIntent
    data object NoCurrentMedicationsToggled : ProfileCompletionIntent
    data object MedicationAdded : ProfileCompletionIntent
    data class MedicationNameChanged(val index: Int, val value: String) : ProfileCompletionIntent
    data class MedicationDosageChanged(val index: Int, val value: String) : ProfileCompletionIntent
    data class MedicationFrequencyChanged(val index: Int, val value: String) : ProfileCompletionIntent
    data class MedicationRemoved(val index: Int) : ProfileCompletionIntent
    data class PreviousSurgeriesChanged(val surgeries: String) : ProfileCompletionIntent
    data class PreviousHospitalizationsChanged(val hospitalizations: String) : ProfileCompletionIntent
    data class MobilityStatusSelected(val status: MobilityStatus) : ProfileCompletionIntent
    data class MobilityNotesChanged(val notes: String) : ProfileCompletionIntent
    data class EmergencyContactNameChanged(val name: String) : ProfileCompletionIntent
    data class EmergencyRelationshipSelected(val relationship: EmergencyRelationship) : ProfileCompletionIntent
    data class EmergencyPhoneNumberChanged(val phoneNumber: String) : ProfileCompletionIntent
    data object BackClicked : ProfileCompletionIntent
    data object ContinueClicked : ProfileCompletionIntent
    data object SkipClicked : ProfileCompletionIntent
    data object RetryClicked : ProfileCompletionIntent
}

sealed interface ProfileCompletionEffect {
    data object NavigateBack : ProfileCompletionEffect
    data object NavigateToHome : ProfileCompletionEffect
}
