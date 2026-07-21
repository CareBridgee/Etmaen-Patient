package com.carenest.presentation.ui.services.list

import androidx.lifecycle.ViewModel
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ServicesViewModel @Inject constructor() :
    ViewModel(),
    StateHolder<ServicesState> by DefaultStateHolder(ServicesState()),
    EffectPublisher<ServicesEffect> by DefaultEffectPublisher() {

    fun onEvent(event: ServicesIntent) {
        when (event) {
            is ServicesIntent.SearchQueryChanged -> updateState {
                copy(searchQuery = event.query)
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
