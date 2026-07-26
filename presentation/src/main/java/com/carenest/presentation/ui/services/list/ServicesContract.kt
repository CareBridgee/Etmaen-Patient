package com.carenest.presentation.ui.services.list

import com.carenest.presentation.model.HealthcareServiceUiModel
import com.carenest.domain.model.home.HealthcareService

data class ServicesState(
    val searchQuery: String = "",
    val services: List<HealthcareService> = emptyList(),
    val filteredServices: List<HealthcareService> = emptyList()
)

sealed interface ServicesIntent {
    data class SearchQueryChanged(val query: String) : ServicesIntent
    data class CategoryClicked(val service: HealthcareServiceUiModel) : ServicesIntent
    data object FilterClicked : ServicesIntent
    data object ChronicCareClicked : ServicesIntent
    data object ConsultationClicked : ServicesIntent
}

sealed interface ServicesEffect {
    data class NavigateToDetails(val service: HealthcareServiceUiModel) : ServicesEffect
    data object OpenFilters : ServicesEffect
    data object OpenCareCoordinator : ServicesEffect
}
