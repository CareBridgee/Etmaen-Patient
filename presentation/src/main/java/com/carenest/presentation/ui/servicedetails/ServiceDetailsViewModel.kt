package com.carenest.presentation.ui.servicedetails

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
            is ServiceDetailsIntent.CategoryReceived -> updateState {
                copy(category = event.category)
            }
            ServiceDetailsIntent.BackClicked -> sendEffect(ServiceDetailsEffect.NavigateBack)
            ServiceDetailsIntent.ShareClicked -> sendEffect(ServiceDetailsEffect.ShareService)
            ServiceDetailsIntent.RequestServiceClicked -> {
                sendEffect(ServiceDetailsEffect.RequestService)
            }
        }
    }
}