package com.carenest.presentation.ui.profile

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carenest.designsystem.theme.Theme
import com.carenest.domain.model.profile.ProfileField
import com.carenest.presentation.core.mvi.ObserveEffect
import com.carenest.presentation.ui.profile.components.ProfileLoadingShimmer
import com.carenest.presentation.ui.profile.steps.AllergiesScreen
import com.carenest.presentation.ui.profile.steps.BasicHealthInfoScreen
import com.carenest.presentation.ui.profile.steps.CurrentMedicationsScreen
import com.carenest.presentation.ui.profile.steps.EmergencyContactScreen
import com.carenest.presentation.ui.profile.steps.MedicalConditionsScreen
import com.carenest.presentation.ui.profile.steps.MedicalHistoryScreen
import com.carenest.presentation.ui.profile.steps.MobilityStatusScreen
import com.carenest.presentation.ui.profile.steps.WelcomeScreen
import com.carenest.presentation.ui.profile.validation.localizedMessage

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
        ProfileLoadingShimmer(ProfileStep.Welcome)
        return
    }

    Box(Modifier.fillMaxSize()) {
        if (state.isLoadingStep || state.currentStep == ProfileStep.FinalStep) {
            ProfileLoadingShimmer(state.currentStep)
        } else {
            ProfileStepContent(
                state = state,
                onEvent = viewModel::onEvent
            )
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

@Composable
private fun ProfileStepContent(
    state: ProfileCompletionState,
    onEvent: (ProfileCompletionIntent) -> Unit
) {
    when (state.currentStep) {
        ProfileStep.Welcome -> WelcomeScreen(
            onBack = { onEvent(ProfileCompletionIntent.BackClicked) },
            onContinue = { onEvent(ProfileCompletionIntent.ContinueClicked) },
            onSkip = { onEvent(ProfileCompletionIntent.SkipClicked) }
        )

        ProfileStep.BasicHealthInfo -> BasicHealthInfoScreen(
            height = state.height,
            weight = state.weight,
            bloodType = state.bloodType,
            heightError = state.validationErrors[ProfileField.Height].localizedMessage(),
            weightError = state.validationErrors[ProfileField.Weight].localizedMessage(),
            bloodTypeError = state.validationErrors[ProfileField.BloodType].localizedMessage(),
            onHeightChange = { onEvent(ProfileCompletionIntent.HeightChanged(it)) },
            onWeightChange = { onEvent(ProfileCompletionIntent.WeightChanged(it)) },
            onBloodTypeChange = { onEvent(ProfileCompletionIntent.BloodTypeChanged(it)) },
            onBack = { onEvent(ProfileCompletionIntent.BackClicked) },
            onContinue = { onEvent(ProfileCompletionIntent.ContinueClicked) },
            isSubmitting = state.isSubmitting
        )

        ProfileStep.MedicalConditions -> MedicalConditionsScreen(
            conditions = state.conditionCatalog,
            selectedConditionIds = state.selectedConditionIds,
            otherConditions = state.otherConditions,
            otherConditionsError = state.validationErrors[ProfileField.OtherConditions]
                .localizedMessage(),
            onConditionToggle = { onEvent(ProfileCompletionIntent.ConditionToggled(it)) },
            onOtherConditionsChange = {
                onEvent(ProfileCompletionIntent.OtherConditionsChanged(it))
            },
            onBack = { onEvent(ProfileCompletionIntent.BackClicked) },
            onContinue = { onEvent(ProfileCompletionIntent.ContinueClicked) },
            isSubmitting = state.isSubmitting
        )

        ProfileStep.Allergies -> AllergiesScreen(
            hasNoKnownAllergies = state.hasNoKnownAllergies,
            allergies = state.allergyCatalog,
            selectedAllergyIds = state.selectedAllergyIds,
            otherAllergies = state.otherAllergies,
            selectionError = state.validationErrors[ProfileField.AllergiesSelection]
                .localizedMessage(),
            otherAllergiesError = state.validationErrors[ProfileField.OtherAllergies]
                .localizedMessage(),
            onNoKnownAllergiesToggle = {
                onEvent(ProfileCompletionIntent.NoKnownAllergiesToggled)
            },
            onAllergyToggle = { onEvent(ProfileCompletionIntent.AllergyToggled(it)) },
            onOtherAllergiesChange = {
                onEvent(ProfileCompletionIntent.OtherAllergiesChanged(it))
            },
            onBack = { onEvent(ProfileCompletionIntent.BackClicked) },
            onContinue = { onEvent(ProfileCompletionIntent.ContinueClicked) },
            isSubmitting = state.isSubmitting
        )

        ProfileStep.CurrentMedications -> CurrentMedicationsScreen(
            hasNoCurrentMedications = state.hasNoCurrentMedications,
            medications = state.currentMedications,
            selectionError = state.validationErrors[ProfileField.MedicationsSelection]
                .localizedMessage(),
            medicationErrors = state.medicationValidationErrors,
            onNoCurrentMedicationsToggle = {
                onEvent(ProfileCompletionIntent.NoCurrentMedicationsToggled)
            },
            onMedicationNameChange = { index, value ->
                onEvent(ProfileCompletionIntent.MedicationNameChanged(index, value))
            },
            onAddMedication = { onEvent(ProfileCompletionIntent.MedicationAdded) },
            onRemoveMedication = { onEvent(ProfileCompletionIntent.MedicationRemoved(it)) },
            onBack = { onEvent(ProfileCompletionIntent.BackClicked) },
            onContinue = { onEvent(ProfileCompletionIntent.ContinueClicked) },
            isSubmitting = state.isSubmitting
        )

        ProfileStep.MedicalHistory -> MedicalHistoryScreen(
            previousSurgeries = state.previousSurgeries,
            previousHospitalizations = state.previousHospitalizations,
            previousSurgeriesError = state.validationErrors[ProfileField.PreviousSurgeries]
                .localizedMessage(),
            previousHospitalizationsError =
                state.validationErrors[ProfileField.PreviousHospitalizations].localizedMessage(),
            onPreviousSurgeriesChange = {
                onEvent(ProfileCompletionIntent.PreviousSurgeriesChanged(it))
            },
            onPreviousHospitalizationsChange = {
                onEvent(ProfileCompletionIntent.PreviousHospitalizationsChanged(it))
            },
            onBack = { onEvent(ProfileCompletionIntent.BackClicked) },
            onContinue = { onEvent(ProfileCompletionIntent.ContinueClicked) },
            isSubmitting = state.isSubmitting
        )

        ProfileStep.MobilityStatus -> MobilityStatusScreen(
            selectedStatus = state.mobilityStatus,
            additionalNotes = state.mobilityNotes,
            statusError = state.validationErrors[ProfileField.MobilityStatus].localizedMessage(),
            notesError = state.validationErrors[ProfileField.MobilityNotes].localizedMessage(),
            onStatusSelected = { onEvent(ProfileCompletionIntent.MobilityStatusSelected(it)) },
            onAdditionalNotesChange = {
                onEvent(ProfileCompletionIntent.MobilityNotesChanged(it))
            },
            onBack = { onEvent(ProfileCompletionIntent.BackClicked) },
            onContinue = { onEvent(ProfileCompletionIntent.ContinueClicked) },
            isSubmitting = state.isSubmitting
        )

        ProfileStep.EmergencyContact -> EmergencyContactScreen(
            contactName = state.emergencyContactName,
            relationship = state.emergencyRelationship,
            phoneNumber = state.emergencyPhoneNumber,
            dataLoaded = state.emergencyContactsLoaded,
            editingUnavailable = state.emergencyContacts.size > 1 &&
                state.emergencyContactId == null,
            contactNameError = state.validationErrors[ProfileField.EmergencyContactName]
                .localizedMessage(),
            relationshipError = state.validationErrors[ProfileField.EmergencyRelationship]
                .localizedMessage(),
            phoneNumberError = state.validationErrors[ProfileField.EmergencyPhoneNumber]
                .localizedMessage(),
            onContactNameChange = {
                onEvent(ProfileCompletionIntent.EmergencyContactNameChanged(it))
            },
            onRelationshipSelected = {
                onEvent(ProfileCompletionIntent.EmergencyRelationshipSelected(it))
            },
            onPhoneNumberChange = {
                onEvent(ProfileCompletionIntent.EmergencyPhoneNumberChanged(it))
            },
            onBack = { onEvent(ProfileCompletionIntent.BackClicked) },
            onContinue = { onEvent(ProfileCompletionIntent.ContinueClicked) },
            isSubmitting = state.isSubmitting
        )

        ProfileStep.FinalStep -> Unit
    }
}
