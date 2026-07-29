package com.carenest.presentation.ui.servicedetails

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.domain.usecase.home.GetServiceDetailsUseCase
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServiceDetailsViewModel @Inject constructor(
    private val getServiceDetailsUseCase: GetServiceDetailsUseCase
) :
    ViewModel(),
    StateHolder<ServiceDetailsState> by DefaultStateHolder(ServiceDetailsState()),
    EffectPublisher<ServiceDetailsEffect> by DefaultEffectPublisher() {


    fun onEvent(event: ServiceDetailsIntent) {
        when (event) {
            is ServiceDetailsIntent.ServiceReceived -> updateState {
                copy(healthcareService = event.service)
            }

            is ServiceDetailsIntent.GetServiceDetails -> getServiceDetails(event.serviceId)

            ServiceDetailsIntent.BackClicked -> sendEffect(ServiceDetailsEffect.NavigateBack)
            ServiceDetailsIntent.ShareClicked -> {
                state.value.healthcareService?.let { service ->
                    sendEffect(ServiceDetailsEffect.ShareService(service.id, service.name, service.description))
                }
            }
            ServiceDetailsIntent.RequestServiceClicked -> {
                state.value.healthcareService?.let { service ->
                    sendEffect(ServiceDetailsEffect.RequestService(service.id))
                }
            }
        }
    }

    private fun getServiceDetails(serviceId: String) {
        viewModelScope.launch {
            getServiceDetailsUseCase(serviceId).onSuccess { service ->
                updateState {
                    copy(healthcareService = service)
                }
                Log.e("service",service.toString())
            }.onFailure {
                updateState {
                    copy(errorMessage = it.message)
                }
            }
        }
    }
}