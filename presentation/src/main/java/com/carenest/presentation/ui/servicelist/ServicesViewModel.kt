package com.carenest.presentation.ui.servicelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.domain.usecase.home.GetServicesUseCase
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

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
                sendEffect(ServicesEffect.NavigateToDetails(event.serviceId))
            }

            ServicesIntent.FilterClicked -> sendEffect(ServicesEffect.OpenFilters)
            ServicesIntent.ChronicCareClicked -> {
                sendEffect(ServicesEffect.NavigateToDetails("CHRONIC_CARE"))
            }
            ServicesIntent.ConsultationClicked -> sendEffect(ServicesEffect.OpenCareCoordinator)
        }
    }
}