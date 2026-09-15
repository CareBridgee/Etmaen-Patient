package com.carenest.presentation.ui.historydetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.domain.usecase.home.GetServiceHistoryDetailsUseCase
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServiceHistoryDetailsViewModel @Inject constructor(
    private val getServiceHistoryDetailsUseCase: GetServiceHistoryDetailsUseCase
) : ViewModel(),
    StateHolder<ServiceHistoryDetailsState> by DefaultStateHolder(ServiceHistoryDetailsState()),
    EffectPublisher<ServiceHistoryDetailsEffect> by DefaultEffectPublisher() {

    fun onEvent(event: ServiceHistoryDetailsIntent) {
        when (event) {
            is ServiceHistoryDetailsIntent.LoadDetails -> loadDetails(event.requestId)
            ServiceHistoryDetailsIntent.BackClicked -> sendEffect(ServiceHistoryDetailsEffect.NavigateBack)
        }
    }

    private fun loadDetails(requestId: String) {
        viewModelScope.launch {
            updateState { copy(isLoading = true, error = null) }
            getServiceHistoryDetailsUseCase(requestId)
                .onSuccess { details ->
                    updateState { copy(serviceHistory = details, isLoading = false) }
                }
                .onFailure { error ->
                    updateState { copy(isLoading = false, error = error.message ?: "Failed to load details") }
                }
        }
    }
}
