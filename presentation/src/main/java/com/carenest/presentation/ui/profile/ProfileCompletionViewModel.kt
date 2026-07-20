package com.carenest.presentation.ui.profile

import androidx.lifecycle.ViewModel
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder

class ProfileCompletionViewModel : ViewModel(),
    StateHolder<ProfileCompletionState> by DefaultStateHolder(ProfileCompletionState()),
    EffectPublisher<ProfileCompletionEffect> by DefaultEffectPublisher() {

    fun onEvent(event: ProfileCompletionIntent) {
        when (event) {
            is ProfileCompletionIntent.HeightChanged -> updateState {
                copy(height = event.height)
            }
            is ProfileCompletionIntent.WeightChanged -> updateState {
                copy(weight = event.weight)
            }
            is ProfileCompletionIntent.BloodTypeChanged -> updateState {
                copy(bloodType = event.bloodType)
            }
            is ProfileCompletionIntent.ConditionToggled -> updateState {
                copy(selectedConditions = selectedConditions.toggle(event.condition))
            }
            is ProfileCompletionIntent.OtherConditionsChanged -> updateState {
                copy(otherConditions = event.conditions)
            }
            ProfileCompletionIntent.NoKnownAllergiesToggled -> updateState {
                copy(
                    hasNoKnownAllergies = !hasNoKnownAllergies,
                    selectedDrugAllergies = emptySet(),
                    selectedFoodAllergies = emptySet(),
                    otherAllergies = ""
                )
            }
            is ProfileCompletionIntent.DrugAllergyToggled -> updateState {
                copy(
                    hasNoKnownAllergies = false,
                    selectedDrugAllergies = selectedDrugAllergies.toggle(event.allergy)
                )
            }
            is ProfileCompletionIntent.FoodAllergyToggled -> updateState {
                copy(
                    hasNoKnownAllergies = false,
                    selectedFoodAllergies = selectedFoodAllergies.toggle(event.allergy)
                )
            }
            is ProfileCompletionIntent.OtherAllergiesChanged -> updateState {
                copy(hasNoKnownAllergies = false, otherAllergies = event.allergies)
            }
            ProfileCompletionIntent.NoCurrentMedicationsToggled -> updateState {
                val noCurrentMedications = !hasNoCurrentMedications
                copy(
                    hasNoCurrentMedications = noCurrentMedications,
                    currentMedications = if (noCurrentMedications) emptyList() else listOf("")
                )
            }
            ProfileCompletionIntent.MedicationAdded -> updateState {
                if (hasNoCurrentMedications) this else copy(
                    currentMedications = currentMedications + ""
                )
            }
            is ProfileCompletionIntent.MedicationChanged -> updateState {
                if (hasNoCurrentMedications || event.index !in currentMedications.indices) {
                    this
                } else {
                    copy(
                        currentMedications = currentMedications.toMutableList().apply {
                            this[event.index] = event.medication
                        }
                    )
                }
            }
            is ProfileCompletionIntent.MedicationRemoved -> updateState {
                if (hasNoCurrentMedications || event.index !in currentMedications.indices) {
                    this
                } else {
                    copy(currentMedications = currentMedications.filterIndexed { index, _ ->
                        index != event.index
                    })
                }
            }
            is ProfileCompletionIntent.PreviousSurgeriesChanged -> updateState {
                copy(previousSurgeries = event.surgeries)
            }
            is ProfileCompletionIntent.PreviousHospitalizationsChanged -> updateState {
                copy(previousHospitalizations = event.hospitalizations)
            }
            is ProfileCompletionIntent.MobilityStatusSelected -> updateState {
                copy(mobilityStatus = event.status)
            }
            is ProfileCompletionIntent.MobilityNotesChanged -> updateState {
                copy(mobilityNotes = event.notes)
            }
            is ProfileCompletionIntent.EmergencyContactNameChanged -> updateState {
                copy(emergencyContactName = event.name)
            }
            is ProfileCompletionIntent.EmergencyRelationshipSelected -> updateState {
                copy(emergencyRelationship = event.relationship)
            }
            is ProfileCompletionIntent.EmergencyPhoneNumberChanged -> updateState {
                copy(emergencyPhoneNumber = event.phoneNumber)
            }
            ProfileCompletionIntent.BackClicked -> navigateBack()
            ProfileCompletionIntent.ContinueClicked -> navigateForward()
            ProfileCompletionIntent.SkipClicked -> {
                sendEffect(ProfileCompletionEffect.NavigateToHome)
            }
        }
    }

    private fun navigateBack() {
        when (currentState.currentStep) {
            ProfileStep.Welcome -> sendEffect(ProfileCompletionEffect.NavigateBack)
            ProfileStep.BasicHealthInfo -> moveTo(ProfileStep.Welcome)
            ProfileStep.MedicalConditions -> moveTo(ProfileStep.BasicHealthInfo)
            ProfileStep.Allergies -> moveTo(ProfileStep.MedicalConditions)
            ProfileStep.CurrentMedications -> moveTo(ProfileStep.Allergies)
            ProfileStep.MedicalHistory -> moveTo(ProfileStep.CurrentMedications)
            ProfileStep.MobilityStatus -> moveTo(ProfileStep.MedicalHistory)
            ProfileStep.EmergencyContact -> moveTo(ProfileStep.MobilityStatus)
            ProfileStep.FinalStep -> moveTo(ProfileStep.EmergencyContact)
        }
    }

    private fun navigateForward() {
        when (currentState.currentStep) {
            ProfileStep.Welcome -> moveTo(ProfileStep.BasicHealthInfo)
            ProfileStep.BasicHealthInfo -> moveTo(ProfileStep.MedicalConditions)
            ProfileStep.MedicalConditions -> moveTo(ProfileStep.Allergies)
            ProfileStep.Allergies -> moveTo(ProfileStep.CurrentMedications)
            ProfileStep.CurrentMedications -> moveTo(ProfileStep.MedicalHistory)
            ProfileStep.MedicalHistory -> moveTo(ProfileStep.MobilityStatus)
            ProfileStep.MobilityStatus -> moveTo(ProfileStep.EmergencyContact)
            ProfileStep.EmergencyContact -> moveTo(ProfileStep.FinalStep)
            ProfileStep.FinalStep -> sendEffect(ProfileCompletionEffect.NavigateToHome)
        }
    }

    private fun moveTo(step: ProfileStep) {
        updateState { copy(currentStep = step) }
    }

    private fun Set<String>.toggle(value: String): Set<String> =
        if (value in this) this - value else this + value
}
