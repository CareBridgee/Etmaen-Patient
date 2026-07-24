package com.carenest.presentation.ui.services.list

import androidx.lifecycle.ViewModel
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.carenest.domain.usecase.home.GetServicesUseCase
import com.carenest.presentation.model.HealthcareServiceUiModel

@HiltViewModel
class ServicesViewModel @Inject constructor(
    private val getServicesUseCase: GetServicesUseCase
) :
    ViewModel(),
    StateHolder<ServicesState> by DefaultStateHolder(ServicesState()),
    EffectPublisher<ServicesEffect> by DefaultEffectPublisher() {

    init {
        loadServices()
    }

    private fun loadServices() {
        viewModelScope.launch {
            val result = getServicesUseCase()
            val services = result.getOrDefault(emptyList())
            updateState {
                copy(
                    services = services,
                    filteredServices = services
                )
            }
        }
    }

    fun onEvent(event: ServicesIntent) {
        when (event) {
            is ServicesIntent.SearchQueryChanged -> {
                val query = event.query
                val filtered = if (query.isBlank()) {
                    currentState.services
                } else {
                    currentState.services.filter { it.name.contains(query, ignoreCase = true) }
                }
                updateState {
                    copy(searchQuery = query, filteredServices = filtered)
                }
            }

            is ServicesIntent.CategoryClicked -> {
                sendEffect(ServicesEffect.NavigateToDetails(event.service))
            }

            ServicesIntent.FilterClicked -> sendEffect(ServicesEffect.OpenFilters)
            ServicesIntent.ChronicCareClicked -> {
                val chronicCareService = HealthcareServiceUiModel(
                    id = "CHRONIC_CARE",
                    name = "Chronic Care",
                    iconResName = "ic_heart_beat",
                    estimatedDurationMinutes = 60,
                    basePrice = 150.0,
                    description = "Comprehensive chronic care management."
                )
                sendEffect(ServicesEffect.NavigateToDetails(chronicCareService))
            }
            ServicesIntent.ConsultationClicked -> sendEffect(ServicesEffect.OpenCareCoordinator)
        }
    }
}
