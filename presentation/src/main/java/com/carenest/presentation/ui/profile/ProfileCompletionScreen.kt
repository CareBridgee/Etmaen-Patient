package com.carenest.presentation.ui.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.carenest.presentation.core.mvi.ObserveEffect
import com.carenest.presentation.ui.profile.steps.AllergiesScreen
import com.carenest.presentation.ui.profile.steps.BasicHealthInfoScreen
import com.carenest.presentation.ui.profile.steps.MedicalConditionsScreen
import com.carenest.presentation.ui.profile.steps.PersonalInfoScreen
import com.carenest.presentation.ui.profile.steps.WelcomeScreen

@Composable
fun ProfileCompletionScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: ProfileCompletionViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

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
        ProfileStep.PersonalInfo -> PersonalInfoScreen(
            firstName = state.firstName,
            lastName = state.lastName,
            dateOfBirth = state.dateOfBirth,
            nationalId = state.nationalId,
            gender = state.gender,
            accountType = state.accountType,
            onFirstNameChange = {
                viewModel.onEvent(ProfileCompletionIntent.FirstNameChanged(it))
            },
            onLastNameChange = {
                viewModel.onEvent(ProfileCompletionIntent.LastNameChanged(it))
            },
            onDateOfBirthChange = {
                viewModel.onEvent(ProfileCompletionIntent.DateOfBirthChanged(it))
            },
            onNationalIdChange = {
                viewModel.onEvent(ProfileCompletionIntent.NationalIdChanged(it))
            },
            onGenderChange = { viewModel.onEvent(ProfileCompletionIntent.GenderChanged(it)) },
            onAccountTypeChange = {
                viewModel.onEvent(ProfileCompletionIntent.AccountTypeChanged(it))
            },
            onBack = { viewModel.onEvent(ProfileCompletionIntent.BackClicked) },
            onContinue = { viewModel.onEvent(ProfileCompletionIntent.ContinueClicked) }
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
    }
}
