package com.carenest.presentation.ui.services.details

import androidx.lifecycle.ViewModel
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ServiceDetailsViewModel @Inject constructor() :
    ViewModel(),
    StateHolder<ServiceDetailsState> by DefaultStateHolder(ServiceDetailsState()),
    EffectPublisher<ServiceDetailsEffect> by DefaultEffectPublisher() {

    fun onEvent(event: ServiceDetailsIntent) {
        when (event) {
            is ServiceDetailsIntent.ServiceReceived -> updateState {
                copy(healthcareService = event.service)
            }

            ServiceDetailsIntent.BackClicked -> sendEffect(ServiceDetailsEffect.NavigateBack)
            ServiceDetailsIntent.ShareClicked -> sendEffect(ServiceDetailsEffect.ShareService)
            ServiceDetailsIntent.RequestServiceClicked -> {
                state.value.healthcareService?.let { service ->
                    sendEffect(ServiceDetailsEffect.RequestService(service))
                }
            }
        }
    }
}
