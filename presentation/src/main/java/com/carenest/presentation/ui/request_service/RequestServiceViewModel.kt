package com.carenest.presentation.ui.request_service

import androidx.lifecycle.ViewModel
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder


class RequestServiceViewModel : ViewModel(),
    StateHolder<RequestServiceUiState> by DefaultStateHolder(RequestServiceUiState()),
    EffectPublisher<RequestServiceEffect> by DefaultEffectPublisher() {

    fun onIntent(intent: RequestServiceIntent) {
        when (intent) {
            is RequestServiceIntent.OnDescriptionChanged -> {
                updateState { copy(description = intent.description) }
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
            RequestServiceIntent.OnSubmitClicked -> TODO()
            RequestServiceIntent.OnBackClicked -> TODO()
            RequestServiceIntent.OnMapClicked -> {
                sendEffect(RequestServiceEffect.NavigateToMap)
            }
        }
    }
}