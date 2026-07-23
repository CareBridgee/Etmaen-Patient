package com.carenest.presentation.ui.profile

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.core.mvi.ObserveEffect
import com.carenest.presentation.ui.profile.steps.*

@Composable
fun ProfileCompletionScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: ProfileCompletionViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            ProfileCompletionEffect.NavigateBack -> onNavigateBack()
            ProfileCompletionEffect.NavigateToHome -> onNavigateToHome()
        }
    }
    BackHandler { viewModel.onEvent(ProfileCompletionIntent.BackClicked) }

    if (state.isInitializing) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Theme.colors.primary)
        }
        return
    }

    Box(Modifier.fillMaxSize()) {
        when (state.currentStep) {
            ProfileStep.Welcome -> WelcomeScreen(
                onBack = { viewModel.onEvent(ProfileCompletionIntent.BackClicked) },
                onContinue = { viewModel.onEvent(ProfileCompletionIntent.ContinueClicked) },
                onSkip = { viewModel.onEvent(ProfileCompletionIntent.SkipClicked) }
            )
            ProfileStep.BasicHealthInfo -> BasicHealthInfoScreen(
                height = state.height,
                weight = state.weight,
                bloodType = state.bloodType,
                onHeightChange = { viewModel.onEvent(ProfileCompletionIntent.HeightChanged(it)) },
                onWeightChange = { viewModel.onEvent(ProfileCompletionIntent.WeightChanged(it)) },
                onBloodTypeChange = { viewModel.onEvent(ProfileCompletionIntent.BloodTypeChanged(it)) },
                onBack = { viewModel.onEvent(ProfileCompletionIntent.BackClicked) },
                onContinue = { viewModel.onEvent(ProfileCompletionIntent.ContinueClicked) },
                isSubmitting = state.isSubmitting
            )
            ProfileStep.MedicalConditions -> MedicalConditionsScreen(
                conditions = state.conditionCatalog,
                selectedConditionKeys = state.selectedConditionKeys,
                otherConditions = state.otherConditions,
                onConditionToggle = { viewModel.onEvent(ProfileCompletionIntent.ConditionToggled(it)) },
                onOtherConditionsChange = { viewModel.onEvent(ProfileCompletionIntent.OtherConditionsChanged(it)) },
                onBack = { viewModel.onEvent(ProfileCompletionIntent.BackClicked) },
                onContinue = { viewModel.onEvent(ProfileCompletionIntent.ContinueClicked) },
                isSubmitting = state.isSubmitting
            )
            ProfileStep.Allergies -> AllergiesScreen(
                hasNoKnownAllergies = state.hasNoKnownAllergies,
                allergies = state.allergyCatalog,
                selectedAllergyKeys = state.selectedAllergyKeys,
                otherAllergies = state.otherAllergies,
                onNoKnownAllergiesToggle = { viewModel.onEvent(ProfileCompletionIntent.NoKnownAllergiesToggled) },
                onAllergyToggle = { viewModel.onEvent(ProfileCompletionIntent.AllergyToggled(it)) },
                onOtherAllergiesChange = { viewModel.onEvent(ProfileCompletionIntent.OtherAllergiesChanged(it)) },
                onBack = { viewModel.onEvent(ProfileCompletionIntent.BackClicked) },
                onContinue = { viewModel.onEvent(ProfileCompletionIntent.ContinueClicked) },
                isSubmitting = state.isSubmitting
            )
            ProfileStep.CurrentMedications -> CurrentMedicationsScreen(
                hasNoCurrentMedications = state.hasNoCurrentMedications,
                medications = state.currentMedications,
                onNoCurrentMedicationsToggle = { viewModel.onEvent(ProfileCompletionIntent.NoCurrentMedicationsToggled) },
                onMedicationNameChange = { index, value ->
                    viewModel.onEvent(ProfileCompletionIntent.MedicationNameChanged(index, value))
                },
                onMedicationDosageChange = { index, value ->
                    viewModel.onEvent(ProfileCompletionIntent.MedicationDosageChanged(index, value))
                },
                onMedicationFrequencyChange = { index, value ->
                    viewModel.onEvent(ProfileCompletionIntent.MedicationFrequencyChanged(index, value))
                },
                onAddMedication = { viewModel.onEvent(ProfileCompletionIntent.MedicationAdded) },
                onRemoveMedication = { viewModel.onEvent(ProfileCompletionIntent.MedicationRemoved(it)) },
                onBack = { viewModel.onEvent(ProfileCompletionIntent.BackClicked) },
                onContinue = { viewModel.onEvent(ProfileCompletionIntent.ContinueClicked) },
                isSubmitting = state.isSubmitting
            )
            ProfileStep.MedicalHistory -> MedicalHistoryScreen(
                previousSurgeries = state.previousSurgeries,
                previousHospitalizations = state.previousHospitalizations,
                onPreviousSurgeriesChange = { viewModel.onEvent(ProfileCompletionIntent.PreviousSurgeriesChanged(it)) },
                onPreviousHospitalizationsChange = { viewModel.onEvent(ProfileCompletionIntent.PreviousHospitalizationsChanged(it)) },
                onBack = { viewModel.onEvent(ProfileCompletionIntent.BackClicked) },
                onContinue = { viewModel.onEvent(ProfileCompletionIntent.ContinueClicked) },
                isSubmitting = state.isSubmitting
            )
            ProfileStep.MobilityStatus -> MobilityStatusScreen(
                selectedStatus = state.mobilityStatus,
                additionalNotes = state.mobilityNotes,
                onStatusSelected = { viewModel.onEvent(ProfileCompletionIntent.MobilityStatusSelected(it)) },
                onAdditionalNotesChange = { viewModel.onEvent(ProfileCompletionIntent.MobilityNotesChanged(it)) },
                onBack = { viewModel.onEvent(ProfileCompletionIntent.BackClicked) },
                onContinue = { viewModel.onEvent(ProfileCompletionIntent.ContinueClicked) },
                isSubmitting = state.isSubmitting
            )
            ProfileStep.EmergencyContact -> EmergencyContactScreen(
                contactName = state.emergencyContactName,
                relationship = state.emergencyRelationship,
                phoneNumber = state.emergencyPhoneNumber,
                dataLoaded = state.emergencyContactsLoaded,
                editingUnavailable = state.emergencyContacts.size > 1 && state.emergencyContactId == null,
                onContactNameChange = { viewModel.onEvent(ProfileCompletionIntent.EmergencyContactNameChanged(it)) },
                onRelationshipSelected = { viewModel.onEvent(ProfileCompletionIntent.EmergencyRelationshipSelected(it)) },
                onPhoneNumberChange = { viewModel.onEvent(ProfileCompletionIntent.EmergencyPhoneNumberChanged(it)) },
                onBack = { viewModel.onEvent(ProfileCompletionIntent.BackClicked) },
                onContinue = { viewModel.onEvent(ProfileCompletionIntent.ContinueClicked) },
                isSubmitting = state.isSubmitting
            )
            ProfileStep.FinalStep -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Theme.colors.primary)
            }
        }

        if (state.isLoadingStep) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Theme.colors.primary)
            }
        }
        state.errorMessage?.let { message ->
            Text(
                text = "$message — tap to retry",
                color = Theme.colors.onPrimary,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Theme.colors.error)
                    .clickable { viewModel.onEvent(ProfileCompletionIntent.RetryClicked) }
                    .padding(Theme.spacing.medium)
            )
        }
    }
}
