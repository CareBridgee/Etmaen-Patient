package com.carenest.presentation.ui.request_service

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.carenest.designsystem.components.toast.ToastHost
import com.carenest.designsystem.components.toast.rememberToastState
import com.carenest.domain.model.LocationDetails
import com.carenest.presentation.core.mvi.ObserveEffect
import com.carenest.presentation.ui.request_service.components.CareRequestScreenContent
import com.carenest.presentation.util.rememberSpeechToTextHelper
import com.carenest.designsystem.R as DesignR

@Composable
fun RequestServiceScreen(
    onNavigateBack: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToAddPatient: () -> Unit,
    onNavigateToServiceSelection: () -> Unit,
    onNavigateToAddressPicker: () -> Unit,
    onSubmitRequestClick: () -> Unit,
    mapResultLocation: LocationDetails? = null,
    onMapResultConsumed: () -> Unit = {},
    viewModel: RequestServiceViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val toastState = rememberToastState()
    val listeningMessage = stringResource(DesignR.string.request_service_listening)

    LaunchedEffect(mapResultLocation) {
        mapResultLocation?.let {
            viewModel.onIntent(RequestServiceIntent.OnLocationDetailsReceived(it))
            onMapResultConsumed()
        }
    }

    val speechToTextHelper = rememberSpeechToTextHelper(
        onResult = { result ->
            viewModel.onIntent(RequestServiceIntent.OnDescriptionChanged(state.description + " " + result))
        },
        onError = { error ->
            viewModel.updateState { copy(isListening = false) }
            toastState.show(error)
        },
        onStarted = {
            viewModel.updateState { copy(isListening = true) }
            toastState.show(listeningMessage)
        }
    )

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            RequestServiceEffect.NavigateBack -> onNavigateBack()
            is RequestServiceEffect.ShowError -> {
                toastState.show(effect.message)
            }
            RequestServiceEffect.NavigateToEditProfile -> onNavigateToEditProfile()
            RequestServiceEffect.NavigateToAddPatient -> onNavigateToAddPatient()
            RequestServiceEffect.NavigateToServiceSelection -> onNavigateToServiceSelection()
            RequestServiceEffect.NavigateToAddressPicker -> onNavigateToAddressPicker()
            RequestServiceEffect.NavigateToMap -> onNavigateToMap()
            RequestServiceEffect.RequestSubmittedSuccessfully -> {
                toastState.show("Request submitted successfully")
                onSubmitRequestClick()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        CareRequestScreenContent(
            state = state,
            onPatientSelected = { viewModel.onIntent(RequestServiceIntent.OnPatientSelected(it)) },
            onEditProfileClick = { viewModel.onIntent(RequestServiceIntent.OnEditProfileClicked) },
            onAddPatientClick = { viewModel.onIntent(RequestServiceIntent.OnAddPatientClicked) },
            onChangeServiceClick = { viewModel.onIntent(RequestServiceIntent.OnChangeServiceClicked) },
            onDescriptionChange = { viewModel.onIntent(RequestServiceIntent.OnDescriptionChanged(it)) },
            onEditAddressClick = { viewModel.onIntent(RequestServiceIntent.OnEditAddressClicked) },
            onFillWithAiClick = { viewModel.onIntent(RequestServiceIntent.OnFillWithAiClicked) },
            onMapClick = { viewModel.onIntent(RequestServiceIntent.OnMapClicked) },
            onMicClick = { speechToTextHelper.startListening() },
            onSubmitClick = { viewModel.onIntent(RequestServiceIntent.OnSubmitClicked) }
        )
        ToastHost(state = toastState)
    }
}

@Preview
@Composable
private fun RequestServiceScreenPreview() {
    RequestServiceScreen(
        onNavigateBack = { TODO() },
        onNavigateToMap = { TODO() },
        onNavigateToEditProfile = { TODO() },
        onNavigateToAddPatient = { TODO() },
        onNavigateToServiceSelection = { TODO() },
        onNavigateToAddressPicker = { TODO() },
        onSubmitRequestClick = { TODO() },
    )
}
