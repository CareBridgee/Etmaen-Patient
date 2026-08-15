package com.carenest.presentation.ui.request_service

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
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

import com.carenest.presentation.navigation.ScreenTopBar

@Composable
fun RequestServiceScreen(
    onNavigateBack: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToAddPatient: () -> Unit,
    onNavigateToServiceSelection: () -> Unit,
    onNavigateToAddressPicker: (LocationDetails?) -> Unit,
    onSubmitRequestClick: (serviceRequestId: String) -> Unit,
    selectServiceId : String? = null,
    isFromAi: Boolean = false,
    mapResultLocation: LocationDetails? = null,
    onMapResultConsumed: () -> Unit = {},
    reloadTrigger: Int = 0,
    viewModel: RequestServiceViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val resources = LocalResources.current
    val toastState = rememberToastState()
    val listeningMessage = stringResource(DesignR.string.request_service_listening)
    val requestSuccessMessage = stringResource(DesignR.string.request_service_success)
    val speechErrorMessage = stringResource(DesignR.string.request_service_speech_error)

    ScreenTopBar(
        title = stringResource(DesignR.string.request_service_title),
        onLeadingClick = onNavigateBack
    )

    LaunchedEffect(Unit, selectServiceId, reloadTrigger, isFromAi) {
        viewModel.onIntent(RequestServiceIntent.OnStart(selectServiceId, isFromAi))
    }

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
        onError = {
            viewModel.updateState { copy(isListening = false) }
            toastState.show(speechErrorMessage)
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
                toastState.show(resources.getString(effect.messageRes))
            }
            RequestServiceEffect.NavigateToEditProfile -> onNavigateToEditProfile()
            RequestServiceEffect.NavigateToAddPatient -> onNavigateToAddPatient()
            is RequestServiceEffect.NavigateToServiceSelection -> onNavigateToServiceSelection()
            RequestServiceEffect.NavigateToAddressPicker -> onNavigateToAddressPicker(state.location)
            RequestServiceEffect.NavigateToMap -> onNavigateToMap()
            is RequestServiceEffect.RequestSubmittedSuccessfully -> {
                toastState.show(requestSuccessMessage)
                onSubmitRequestClick(effect.serviceRequestId)
            }
        }
    }

        Box(modifier = Modifier.fillMaxSize()) {
            CareRequestScreenContent(
                state = state,
                isFromAi = isFromAi,
                onPatientSelected = { viewModel.onIntent(RequestServiceIntent.OnPatientSelected(it)) },
                onEditProfileClick = { viewModel.onIntent(RequestServiceIntent.OnEditProfileClicked) },
                onAddPatientClick = { viewModel.onIntent(RequestServiceIntent.OnAddPatientClicked) },
                onChangeServiceClick = { viewModel.onIntent(RequestServiceIntent.OnChangeServiceClicked) },
                onDescriptionChange = { viewModel.onIntent(RequestServiceIntent.OnDescriptionChanged(it)) },
                onEditAddressClick = { viewModel.onIntent(RequestServiceIntent.OnEditAddressClicked) },
                onFillWithAiClick = { viewModel.onIntent(RequestServiceIntent.OnFillWithAiClicked) },
                onMapClick = { viewModel.onIntent(RequestServiceIntent.OnMapClicked) },
                onMicClick = { speechToTextHelper.startListening() },
                onSubmitClick = { viewModel.onIntent(RequestServiceIntent.OnSubmitClicked) },
            )
            ToastHost(state = toastState)
        }

}

@Preview
@Composable
private fun RequestServiceScreenPreview() {
    RequestServiceScreen(
        onNavigateBack = { },
        onNavigateToMap = { },
        onNavigateToEditProfile = { },
        onNavigateToAddPatient = { },
        onNavigateToServiceSelection = { },
        onNavigateToAddressPicker = { },
        onSubmitRequestClick = { },
    )
}
