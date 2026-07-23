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
import com.carenest.domain.model.PaymentMethod
import com.carenest.presentation.ui.request_service.RequestServiceUiState

@Composable
fun CareRequestScreenContent(
    state: RequestServiceUiState,
    onPatientSelected: (Patient) -> Unit,
    onEditProfileClick: () -> Unit,
    onAddPatientClick: () -> Unit,
    onChangeServiceClick: () -> Unit,
    onDescriptionChange: (String) -> Unit,
    onEditAddressClick: () -> Unit,
    onPaymentMethodSelected: (PaymentMethod) -> Unit,
    onFillWithAiClick: () -> Unit,
    onMapClick: () -> Unit,
    onSubmitClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showPermissionHandler by remember { mutableStateOf(false) }
    var showRationale by remember { mutableStateOf(false) }

    if (showPermissionHandler) {
        LocationPermissionHandler(
            onPermissionGranted = {
                showPermissionHandler = false
                onMapClick()
            },
            onPermissionDenied = {
                showPermissionHandler = false
                showRationale = true
            },
            showRationale = showRationale,
            onRationaleDismissed = {
                showRationale = false
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
        AiFillButton(onClick = onFillWithAiClick)

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
            onTextChange = onDescriptionChange
        )

        AddressSection(
            location = state.location,
            onEditClick = onEditAddressClick,
            onMapClick = { showPermissionHandler = true }
        )

        PaymentMethodSection(
            paymentMethods = state.paymentMethods,
            onMethodSelected = onPaymentMethodSelected
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
            onPaymentMethodSelected = {},
            onFillWithAiClick = {},
            onMapClick = {},
            onSubmitClick = {}
        )
    }
}
