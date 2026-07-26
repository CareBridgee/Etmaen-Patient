package com.carenest.presentation.ui.request_service

import androidx.lifecycle.ViewModel
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import com.carenest.presentation.model.toDomainModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RequestServiceViewModel @Inject constructor(): ViewModel(),
    StateHolder<RequestServiceUiState> by DefaultStateHolder(
        RequestServiceUiState(
            location = com.carenest.domain.model.LocationDetails(
                address = "Cairo, Egypt",
                apartment = "",
                district = "",
                latitude = 30.0444,
                longitude = 31.2357
            )
        )
    ),
    EffectPublisher<RequestServiceEffect> by DefaultEffectPublisher() {

    fun onIntent(intent: RequestServiceIntent) {
        when (intent) {
            is RequestServiceIntent.OnStart -> {
                intent.service?.let { uiModel ->
                    updateState { copy(selectedService = uiModel.toDomainModel()) }
                }
            }

            is RequestServiceIntent.OnDescriptionChanged -> {
                updateState { copy(description = intent.description, isListening = false) }
            }

            is RequestServiceIntent.OnPatientSelected -> {
                updateState { copy(selectedPatient = intent.patient) }
            }

            is RequestServiceIntent.OnPaymentMethodSelected -> {
                updateState { copy(selectedPaymentMethod = intent.paymentMethod) }
            }

            RequestServiceIntent.OnAddPatientClicked -> {
                sendEffect(RequestServiceEffect.NavigateToAddPatient)
            }

            RequestServiceIntent.OnChangeServiceClicked -> {
                sendEffect(RequestServiceEffect.NavigateToServiceSelection(state.value.selectedService))
            }

            RequestServiceIntent.OnEditAddressClicked -> {
                sendEffect(RequestServiceEffect.NavigateToAddressPicker)
            }

            RequestServiceIntent.OnEditProfileClicked -> {
                sendEffect(RequestServiceEffect.NavigateToEditProfile)
            }

            RequestServiceIntent.OnFillWithAiClicked -> {
                // Implement AI fill logic if needed
            }

            RequestServiceIntent.OnHelpClicked -> {
                // Implement help logic if needed
            }

            RequestServiceIntent.OnSubmitClicked -> {
                sendEffect(RequestServiceEffect.RequestSubmittedSuccessfully)
            }

            RequestServiceIntent.OnBackClicked -> {
                sendEffect(RequestServiceEffect.NavigateBack)
            }

            RequestServiceIntent.OnMapClicked -> {
                sendEffect(RequestServiceEffect.NavigateToMap)
            }

            is RequestServiceIntent.OnLocationDetailsReceived -> {
                updateState { copy(location = intent.locationDetails) }
            }
        }
    }
}
