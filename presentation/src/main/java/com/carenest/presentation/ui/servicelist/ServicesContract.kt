package com.carenest.presentation.ui.servicelist

import com.carenest.domain.model.home.HealthcareService
import com.carenest.presentation.core.util.UiText

data class ServicesState(
    val searchQuery: String = "",
    val services: List<HealthcareService> = emptyList(),
    val filteredServices: List<HealthcareService> = emptyList(),
    val userName: String = "",
    val userImageUrl: String? = null,
    val isLoading: Boolean = true,
    val errorMessage: UiText? = null
)

sealed interface ServicesIntent {
    data class SearchQueryChanged(val query: String) : ServicesIntent
    data class CategoryClicked(val serviceId: String) : ServicesIntent
    data object FilterClicked : ServicesIntent
    data object ChronicCareClicked : ServicesIntent
    data object ConsultationClicked : ServicesIntent
}

sealed interface ServicesEffect {
    data class NavigateToDetails(val serviceId: String) : ServicesEffect
    data object OpenFilters : ServicesEffect
    data object OpenCareCoordinator : ServicesEffect
}
