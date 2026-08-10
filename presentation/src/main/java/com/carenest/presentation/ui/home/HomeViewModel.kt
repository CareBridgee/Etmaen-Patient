package com.carenest.presentation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.domain.model.home.HealthcareService
import com.carenest.domain.usecase.home.GetServicesUseCase
import com.carenest.domain.usecase.home.GetUserRequestHistoryUseCase
import com.carenest.domain.usecase.user.GetCurrentUserUseCase
import com.carenest.domain.usecase.user.ObserveCurrentUserUseCase
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCurrentUser: GetCurrentUserUseCase,
    private val observeCurrentUser: ObserveCurrentUserUseCase,
    private val getServicesUseCase: GetServicesUseCase,
    private val getServiceHistoryUseCase: GetUserRequestHistoryUseCase
) : ViewModel(),
    StateHolder<HomeState> by DefaultStateHolder(HomeState()),
    EffectPublisher<HomeEffect> by DefaultEffectPublisher() {

    init {
        observeUser()
        loadHomeData()
    }

    fun onEvent(event: HomeIntent) {
        when (event) {
            is HomeIntent.SearchQueryChanged -> handleSearchQueryChanged(event.query)
            HomeIntent.ClearSearch -> handleSearchQueryChanged("")
            HomeIntent.StartAIChatClicked -> sendEffect(HomeEffect.NavigateToAIChat)
            HomeIntent.ViewAllServicesClicked -> sendEffect(HomeEffect.NavigateToServices)
            is HomeIntent.ServiceClicked -> {
                if (event.service.id == "ACTIVE_REQUEST") {
                    onEvent(HomeIntent.ActiveRequestClicked)
                } else {
                    sendEffect(HomeEffect.NavigateToServiceDetails(event.service.id))
                }
            }
            HomeIntent.ManageAllHistoryClicked -> sendEffect(HomeEffect.NavigateToHistory)
            HomeIntent.ActiveRequestClicked -> {
                currentState.activeRequest?.let {
                    sendEffect(HomeEffect.NavigateToActiveRequest(it.serviceRequestId, it.status))
                }
            }
            is HomeIntent.HistoryItemClicked -> sendEffect(HomeEffect.NavigateToServiceHistoryDetails(event.serviceHistory.serviceRequestId))
            HomeIntent.RetryClicked -> loadHomeData()
        }
    }

    private fun observeUser() {
        viewModelScope.launch {
            observeCurrentUser().collect { user ->
                updateState { copy(user = user) }
            }
        }
    }

    fun loadHomeData() {
        viewModelScope.launch {
            updateState { copy(isLoading = true, isError = false, errorMessage = null) }

            try {
                val userDeferred = async { getCurrentUser() }
                val servicesDeferred = async { getServicesUseCase() }
                val bookingDeferred = async { getServiceHistoryUseCase() }

                val userResult = userDeferred.await()
                val servicesResult = servicesDeferred.await()
                val bookingResult = bookingDeferred.await()
                
                if (userResult.isFailure && servicesResult.isFailure && bookingResult.isFailure) {
                    val errorMsg = userResult.exceptionOrNull()?.message ?: "Failed to load home data"
                    updateState { copy(isLoading = false, isError = true, errorMessage = errorMsg) }
                    return@launch
                }

                val user = userResult.getOrNull()
                val services = servicesResult.getOrDefault(emptyList())
                val booking = bookingResult.getOrNull() ?: emptyList()

                val activeRequest = booking.find { 
                    it.status.equals("ACCEPTED", ignoreCase = true) ||
                    it.status.equals("SEARCHING", ignoreCase = true)
                }

                updateState {
                    copy(
                        user = user ?: currentState.user,
                        allServices = services,
                        filteredServices = applyActiveRequestFilter(services, activeRequest, currentState.searchQuery),
                        upcomingBooking = booking.take(1),
                        activeRequest = activeRequest,
                        isLoading = false,
                        isError = false
                    )
                }
            } catch (e: Exception) {
                updateState {
                    copy(
                        isLoading = false,
                        isError = true,
                        errorMessage = e.message ?: "An unexpected error occurred"
                    )
                }
            }
        }
    }

    private fun handleSearchQueryChanged(query: String) {
        val filtered = applyActiveRequestFilter(currentState.allServices, currentState.activeRequest, query)
        updateState {
            copy(
                searchQuery = query,
                filteredServices = filtered
            )
        }
    }

    private fun applyActiveRequestFilter(
        services: List<HealthcareService>,
        activeRequest: com.carenest.domain.model.history.ServiceHistory?,
        query: String
    ): List<HealthcareService> {
        val trimmedQuery = query.trim()
        val baseFiltered = if (trimmedQuery.isBlank()) {
            services.take(5)
        } else {
            services.filter { it.name.contains(trimmedQuery, ignoreCase = true) }
        }

        return if (activeRequest != null) {
            val trackService = HealthcareService(
                id = "ACTIVE_REQUEST",
                name = "Ongoing: ${activeRequest.serviceName}",
                estimatedDurationMinutes = 0,
                basePrice = 0.0,
                description = "Track your current service request",
                iconResName = "ic_tracking"
            )
            // Remove the service that is currently active and add the tracking one at the top
            listOf(trackService) + baseFiltered.filter { it.id != activeRequest.serviceTypeId }
        } else {
            baseFiltered
        }
    }
}
