package com.carenest.presentation.ui.services.list

import kotlinx.serialization.Serializable

@Serializable
enum class ServiceCategory {
    GENERAL_NURSING,
    INJECTION,
    PHYSICAL_THERAPY,
    WOUND_CARE,
    POST_NATAL,
    ELDERLY_CARE,
    IV_DRIP,
    VACCINATIONS,
    CHRONIC_CARE,
}

data class ServicesState(
    val searchQuery: String = "",
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
