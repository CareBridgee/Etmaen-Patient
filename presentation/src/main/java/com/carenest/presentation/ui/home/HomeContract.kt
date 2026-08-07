package com.carenest.presentation.ui.home

import com.carenest.domain.model.history.ServiceHistory
import com.carenest.domain.model.home.HealthcareService
import com.carenest.domain.model.home.User

sealed interface HomeIntent {
    data class SearchQueryChanged(val query: String) : HomeIntent
    data object ClearSearch : HomeIntent
    data object StartAIChatClicked : HomeIntent
    data object ViewAllServicesClicked : HomeIntent
    data class ServiceClicked(val service: HealthcareService) : HomeIntent
    data object ManageAllHistoryClicked : HomeIntent
    data class HistoryItemClicked(val serviceHistory: ServiceHistory) : HomeIntent
    data object RetryClicked : HomeIntent
}

data class HomeState(
    val user: User? = null,
    val allServices: List<HealthcareService> = emptyList(),
    val filteredServices: List<HealthcareService> = emptyList(),
    val searchQuery: String = "",
    val upcomingBooking: List<ServiceHistory> = emptyList(),
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val errorMessage: String? = null
) {
    val isSearchActive: Boolean
        get() = searchQuery.trim().isNotBlank()

    val isSearchEmpty: Boolean
        get() = isSearchActive && filteredServices.isEmpty() && !isLoading && !isError

    val isServicesEmpty: Boolean
        get() = !isSearchActive && allServices.isEmpty() && !isLoading && !isError

    val isHistoryEmpty: Boolean
        get() = upcomingBooking.isEmpty() && !isLoading && !isError
}

sealed class HomeEffect {
    object NavigateToServices : HomeEffect()
    object NavigateToHistory : HomeEffect()
    object NavigateToAIChat : HomeEffect()
    data class NavigateToServiceDetails(val serviceId: String) : HomeEffect()
    data class NavigateToServiceHistoryDetails(val requestId: String) : HomeEffect()
    data class ShowToast(
        val message: String, 
        val type: com.carenest.designsystem.components.toast.ToastType = com.carenest.designsystem.components.toast.ToastType.Info
    ) : HomeEffect()
}
