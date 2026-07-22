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
import com.carenest.domain.model.home.ServiceCategory
import com.carenest.domain.usecase.home.GetServicesUseCase

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
                sendEffect(ServicesEffect.NavigateToDetails(event.category))
            }

            ServicesIntent.FilterClicked -> sendEffect(ServicesEffect.OpenFilters)
            ServicesIntent.ChronicCareClicked -> {
                sendEffect(ServicesEffect.NavigateToDetails(ServiceCategory.CHRONIC_CARE))
            }
            ServicesIntent.ConsultationClicked -> sendEffect(ServicesEffect.OpenCareCoordinator)
        }
    }
}
