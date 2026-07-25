package com.carenest.presentation.ui.request_service

import androidx.lifecycle.ViewModel
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
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
            is RequestServiceIntent.OnDescriptionChanged -> {
                updateState { copy(description = intent.description, isListening = false) }
            }
            is RequestServiceIntent.OnPatientSelected -> {
                updateState { copy(selectedPatient = intent.patient) }
            }
            is RequestServiceIntent.OnPaymentMethodSelected -> {
                updateState { copy(selectedPaymentMethod = intent.paymentMethod) }
            }
            RequestServiceIntent.OnAddPatientClicked -> TODO()
            RequestServiceIntent.OnChangeServiceClicked -> TODO()
            RequestServiceIntent.OnEditAddressClicked -> TODO()
            RequestServiceIntent.OnEditProfileClicked -> TODO()
            RequestServiceIntent.OnFillWithAiClicked -> TODO()
            RequestServiceIntent.OnHelpClicked -> TODO()
            RequestServiceIntent.OnSubmitClicked -> {
                sendEffect(RequestServiceEffect.RequestSubmittedSuccessfully)
            }
            RequestServiceIntent.OnBackClicked -> TODO()
            RequestServiceIntent.OnMapClicked -> {
                sendEffect(RequestServiceEffect.NavigateToMap)
            }
            is RequestServiceIntent.OnLocationDetailsReceived -> {
                updateState { copy(location = intent.locationDetails) }
            }
        }
    }
}