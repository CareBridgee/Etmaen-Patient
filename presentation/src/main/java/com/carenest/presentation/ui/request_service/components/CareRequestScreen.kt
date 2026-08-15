package com.carenest.presentation.ui.request_service.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.carenest.designsystem.R
import com.carenest.designsystem.components.button.PrimaryButton
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.domain.model.Patient
import com.carenest.presentation.ui.request_service.RequestServiceUiState
import com.carenest.presentation.util.AudioPermissionHandler

@Composable
fun CareRequestScreenContent(
    state: RequestServiceUiState,
    isFromAi: Boolean = false,
    onPatientSelected: (Patient) -> Unit,
    onEditProfileClick: () -> Unit,
    onAddPatientClick: () -> Unit,
    onChangeServiceClick: () -> Unit,
    onDescriptionChange: (String) -> Unit,
    onEditAddressClick: () -> Unit,
    onFillWithAiClick: () -> Unit,
    onMapClick: () -> Unit,
    onMicClick: () -> Unit,
    onSubmitClick: () -> Unit,
    modifier: Modifier = Modifier,
) {

    var showAudioPermissionHandler by remember { mutableStateOf(false) }
    var showAudioRationale by remember { mutableStateOf(false) }

    if (showAudioPermissionHandler) {
        AudioPermissionHandler(
            onPermissionGranted = {
                showAudioPermissionHandler = false
                onMicClick()
            },
            onPermissionDenied = {
                showAudioPermissionHandler = false
                showAudioRationale = true
            },
            showRationale = showAudioRationale,
            onRationaleDismissed = {
                showAudioRationale = false
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Theme.colors.backGround)
            .verticalScroll(rememberScrollState())
            .padding(Theme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.large),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isFromAi) {
            AiFillButton(
                onClick = onFillWithAiClick,
                enabled = true
            )
        }

        PatientSelectionSection(
            patients = state.patients,
            selectedPatientId = state.selectedPatient?.id,
            onPatientSelected = onPatientSelected,
            onEditProfileClick = onEditProfileClick,
            onAddPatientClick = onAddPatientClick
        )

        ChosenServiceCard(
            service = state.selectedService,
            onDetailsClick = {}, // Placeholder
            onChangeClick = onChangeServiceClick
        )

        SituationDescriptionField(
            text = state.description,
            onTextChange = onDescriptionChange,
            isListening = state.isListening,
            onMicClick = { showAudioPermissionHandler = true }
        )

        AddressSection(
            location = state.location,
            onEditClick = onEditAddressClick,
            onMapClick = onMapClick
        )


        PrimaryButton(
            caption = stringResource(id = R.string.request_service_submit),
            onClick = onSubmitClick,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CareRequestScreenPreview() {
    SpTheme {
        CareRequestScreenContent(
            state = RequestServiceUiState(),
            onPatientSelected = {},
            onEditProfileClick = {},
            onAddPatientClick = {},
            onChangeServiceClick = {},
            onDescriptionChange = {},
            onEditAddressClick = {},
            onFillWithAiClick = {},
            onMapClick = {},
            onMicClick = {},
            onSubmitClick = {},
        )
    }
}
