package com.carenest.presentation.ui.requestservice.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.carenest.designsystem.R
import com.carenest.designsystem.components.button.PrimaryButton
import com.carenest.designsystem.components.shimmer.ShimmerPlaceholder
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.domain.model.Patient
import com.carenest.presentation.ui.requestservice.RequestServiceUiState
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
    onPaymentMethodSelected: (com.carenest.domain.model.payment.ServicePaymentMethod) -> Unit,
    onWalletCreditRetryClick: () -> Unit,
    onAddWalletCreditClick: () -> Unit,
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

    if (state.isLoading) {
        RequestServiceLoadingShimmer(modifier = modifier)
        return
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

        PaymentSelectionSection(
            paymentMethods = state.paymentMethods,
            selectedPaymentMethod = state.selectedPaymentMethod,
            walletCreditState = state.walletCreditState,
            servicePrice = state.selectedService?.basePrice,
            onPaymentMethodSelected = onPaymentMethodSelected,
            onWalletRetryClick = onWalletCreditRetryClick,
            onAddWalletCreditClick = onAddWalletCreditClick,
            modifier = Modifier.fillMaxWidth(),
        )

        PrimaryButton(
            caption = stringResource(id = R.string.request_service_submit),
            onClick = onSubmitClick,
            modifier = Modifier.fillMaxWidth(),
            isDisabled = state.isSubmitting,
            isLoading = state.isSubmitting,
        )
    }
}

@Composable
private fun RequestServiceLoadingShimmer(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Theme.colors.backGround)
            .verticalScroll(rememberScrollState())
            .padding(Theme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.large),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Theme.colors.surface, RoundedCornerShape(20.dp))
                .padding(Theme.spacing.medium),
        ) {
            ShimmerPlaceholder(
                modifier = Modifier
                    .fillMaxWidth(0.42f)
                    .height(18.dp),
            )
            Spacer(modifier = Modifier.height(16.dp))
            repeat(2) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                ) {
                    ShimmerPlaceholder(
                        modifier = Modifier.size(52.dp),
                        shape = CircleShape,
                    )
                    ShimmerPlaceholder(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxWidth(0.55f)
                            .height(18.dp),
                    )
                }
            }
        }

        listOf(126.dp, 148.dp, 118.dp, 108.dp).forEach { height ->
            ShimmerPlaceholder(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height),
                shape = RoundedCornerShape(20.dp),
            )
        }

        ShimmerPlaceholder(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
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
            onPaymentMethodSelected = {},
            onWalletCreditRetryClick = {},
            onAddWalletCreditClick = {},
            onMicClick = {},
            onSubmitClick = {},
        )
    }
}
