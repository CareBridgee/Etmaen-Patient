package com.carenest.presentation.ui.servicelist

import com.carenest.domain.model.home.ServiceCategory
import com.carenest.domain.model.home.HealthcareService

data class ServicesState(
    val searchQuery: String = "",
    val services: List<HealthcareService> = emptyList(),
    val filteredServices: List<HealthcareService> = emptyList()
)

sealed interface ServicesIntent {
    data class SearchQueryChanged(val query: String) : ServicesIntent
    data class CategoryClicked(val category: ServiceCategory) : ServicesIntent
    data object FilterClicked : ServicesIntent
    data object ChronicCareClicked : ServicesIntent
    data object ConsultationClicked : ServicesIntent
}

sealed interface ServicesEffect {
    data class NavigateToDetails(val category: ServiceCategory) : ServicesEffect
    data object OpenFilters : ServicesEffect
    data object OpenCareCoordinator : ServicesEffect
}
