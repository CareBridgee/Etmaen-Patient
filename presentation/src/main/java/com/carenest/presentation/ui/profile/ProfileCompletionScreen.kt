package com.carenest.presentation.ui.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.carenest.presentation.core.mvi.ObserveEffect
import com.carenest.presentation.ui.profile.steps.AllergiesScreen
import com.carenest.presentation.ui.profile.steps.BasicHealthInfoScreen
import com.carenest.presentation.ui.profile.steps.CurrentMedicationsScreen
import com.carenest.presentation.ui.profile.steps.EmergencyContactScreen
import com.carenest.presentation.ui.profile.steps.MedicalConditionsScreen
import com.carenest.presentation.ui.profile.steps.MedicalHistoryScreen
import com.carenest.presentation.ui.profile.steps.MobilityStatusScreen
import com.carenest.presentation.ui.profile.steps.WelcomeScreen

@Composable
fun ProfileCompletionScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: ProfileCompletionViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            ProfileCompletionEffect.NavigateBack -> onNavigateBack()
            ProfileCompletionEffect.NavigateToHome -> onNavigateToHome()
        }
    }

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
            onBloodTypeChange = {
                viewModel.onEvent(ProfileCompletionIntent.BloodTypeChanged(it))
            },
            onBack = { viewModel.onEvent(ProfileCompletionIntent.BackClicked) },
            onContinue = { viewModel.onEvent(ProfileCompletionIntent.ContinueClicked) }
        )
        ProfileStep.MedicalConditions -> MedicalConditionsScreen(
            selectedConditions = state.selectedConditions,
            otherConditions = state.otherConditions,
            onConditionToggle = {
                viewModel.onEvent(ProfileCompletionIntent.ConditionToggled(it))
            },
            onOtherConditionsChange = {
                viewModel.onEvent(ProfileCompletionIntent.OtherConditionsChanged(it))
            },
            onBack = { viewModel.onEvent(ProfileCompletionIntent.BackClicked) },
            onContinue = { viewModel.onEvent(ProfileCompletionIntent.ContinueClicked) }
        )
        ProfileStep.Allergies -> AllergiesScreen(
            hasNoKnownAllergies = state.hasNoKnownAllergies,
            selectedDrugAllergies = state.selectedDrugAllergies,
            selectedFoodAllergies = state.selectedFoodAllergies,
            otherAllergies = state.otherAllergies,
            onNoKnownAllergiesToggle = {
                viewModel.onEvent(ProfileCompletionIntent.NoKnownAllergiesToggled)
            },
            onDrugAllergyToggle = {
                viewModel.onEvent(ProfileCompletionIntent.DrugAllergyToggled(it))
            },
            onFoodAllergyToggle = {
                viewModel.onEvent(ProfileCompletionIntent.FoodAllergyToggled(it))
            },
            onOtherAllergiesChange = {
                viewModel.onEvent(ProfileCompletionIntent.OtherAllergiesChanged(it))
            },
            onBack = { viewModel.onEvent(ProfileCompletionIntent.BackClicked) },
            onContinue = { viewModel.onEvent(ProfileCompletionIntent.ContinueClicked) }
        )
        ProfileStep.CurrentMedications -> CurrentMedicationsScreen(
            hasNoCurrentMedications = state.hasNoCurrentMedications,
            medications = state.currentMedications,
            onNoCurrentMedicationsToggle = {
                viewModel.onEvent(ProfileCompletionIntent.NoCurrentMedicationsToggled)
            },
            onMedicationChange = { index, medication ->
                viewModel.onEvent(
                    ProfileCompletionIntent.MedicationChanged(index, medication)
                )
            },
            onAddMedication = {
                viewModel.onEvent(ProfileCompletionIntent.MedicationAdded)
            },
            onRemoveMedication = {
                viewModel.onEvent(ProfileCompletionIntent.MedicationRemoved(it))
            },
            onBack = { viewModel.onEvent(ProfileCompletionIntent.BackClicked) },
            onContinue = { viewModel.onEvent(ProfileCompletionIntent.ContinueClicked) }
        )
        ProfileStep.MedicalHistory -> MedicalHistoryScreen(
            previousSurgeries = state.previousSurgeries,
            previousHospitalizations = state.previousHospitalizations,
            onPreviousSurgeriesChange = {
                viewModel.onEvent(ProfileCompletionIntent.PreviousSurgeriesChanged(it))
            },
            onPreviousHospitalizationsChange = {
                viewModel.onEvent(ProfileCompletionIntent.PreviousHospitalizationsChanged(it))
            },
            onBack = { viewModel.onEvent(ProfileCompletionIntent.BackClicked) },
            onContinue = { viewModel.onEvent(ProfileCompletionIntent.ContinueClicked) }
        )
        ProfileStep.MobilityStatus -> MobilityStatusScreen(
            selectedStatus = state.mobilityStatus,
            additionalNotes = state.mobilityNotes,
            onStatusSelected = {
                viewModel.onEvent(ProfileCompletionIntent.MobilityStatusSelected(it))
            },
            onAdditionalNotesChange = {
                viewModel.onEvent(ProfileCompletionIntent.MobilityNotesChanged(it))
            },
            onBack = { viewModel.onEvent(ProfileCompletionIntent.BackClicked) },
            onContinue = { viewModel.onEvent(ProfileCompletionIntent.ContinueClicked) }
        )
        ProfileStep.EmergencyContact -> EmergencyContactScreen(
            contactName = state.emergencyContactName,
            relationship = state.emergencyRelationship,
            phoneNumber = state.emergencyPhoneNumber,
            onContactNameChange = {
                viewModel.onEvent(ProfileCompletionIntent.EmergencyContactNameChanged(it))
            },
            onRelationshipSelected = {
                viewModel.onEvent(ProfileCompletionIntent.EmergencyRelationshipSelected(it))
            },
            onPhoneNumberChange = {
                viewModel.onEvent(ProfileCompletionIntent.EmergencyPhoneNumberChanged(it))
            },
            onBack = { viewModel.onEvent(ProfileCompletionIntent.BackClicked) },
            onContinue = { viewModel.onEvent(ProfileCompletionIntent.ContinueClicked) }
        )
        ProfileStep.FinalStep -> LaunchedEffect(Unit) {
            viewModel.onEvent(ProfileCompletionIntent.ContinueClicked)
        }
    }
}
