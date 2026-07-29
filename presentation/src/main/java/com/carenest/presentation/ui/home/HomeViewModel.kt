package com.carenest.presentation.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.domain.model.home.HealthcareService
import com.carenest.domain.usecase.home.GetServicesUseCase
import com.carenest.domain.usecase.home.GetUserRequestHistoryUseCase
import com.carenest.domain.usecase.home.GetUserUseCase
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
    private val getUserUseCase: GetUserUseCase,
    private val getServicesUseCase: GetServicesUseCase,
    private val getUpcomingBookingUseCase: GetUserRequestHistoryUseCase
) : ViewModel(),
    StateHolder<HomeState> by DefaultStateHolder(HomeState()),
    EffectPublisher<HomeEffect> by DefaultEffectPublisher() {

    companion object {
        private const val TAG = "HomeViewModel"
    }

    init {
        loadHomeData()
    }

    fun onEvent(event: HomeIntent) {
        when (event) {
            is HomeIntent.SearchQueryChanged -> handleSearchQueryChanged(event.query)
            HomeIntent.ClearSearch -> handleSearchQueryChanged("")
            HomeIntent.StartAIChatClicked -> sendEffect(HomeEffect.NavigateToAIChat)
            HomeIntent.ViewAllServicesClicked -> sendEffect(HomeEffect.NavigateToServices)
            is HomeIntent.ServiceClicked -> {
                if (event.service.id == "MORE_SERVICES") {
                    sendEffect(HomeEffect.NavigateToServices)
                } else {
                    sendEffect(HomeEffect.NavigateToServiceDetails(event.service.id))
                }
            }
            HomeIntent.ManageBookingsClicked -> sendEffect(HomeEffect.NavigateToBookings)
            is HomeIntent.BookingClicked -> sendEffect(HomeEffect.NavigateToBookings)
            HomeIntent.RetryClicked -> loadHomeData()
            HomeIntent.NotificationClicked -> sendEffect(HomeEffect.ShowToast("No new notifications"))
        }
    }

    fun loadHomeData() {
        viewModelScope.launch {
            updateState { copy(isLoading = true, isError = false, errorMessage = null) }

            try {
                val userDeferred = async { getUserUseCase() }
                val servicesDeferred = async { getServicesUseCase() }
                val bookingDeferred = async { getUpcomingBookingUseCase() }

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

                val trimmedQuery = currentState.searchQuery.trim()
                val filtered = if (trimmedQuery.isBlank()) {
                    val topServices = services.take(5).toMutableList()
                    topServices.add(
                        HealthcareService(
                            id = "MORE_SERVICES",
                            name = "More Services",
                            iconResName = "ic_services",
                            estimatedDurationMinutes = 1,
                            basePrice = 1.0,
                            description = "",
                        )
                    )
                    topServices
                } else {
                    services.filter { it.name.contains(trimmedQuery, ignoreCase = true) }
                }

                updateState {
                    copy(
                        user = user,
                        allServices = services,
                        filteredServices = filtered,
                        upcomingBooking = booking,
                        isLoading = false,
                        isError = false
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading home data", e)
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
        val trimmedQuery = query.trim()
        val services = currentState.allServices
        val filtered = if (trimmedQuery.isBlank()) {
            val topServices = services.take(5).toMutableList()
            topServices.add(
                HealthcareService(
                    id = "MORE_SERVICES",
                    name = "More Services",
                    iconResName = "ic_services",
                    estimatedDurationMinutes = 1,
                    basePrice = 1.0,
                    description = "",
                )
            )
            topServices
        } else {
            services.filter { it.name.contains(trimmedQuery, ignoreCase = true) }
        }

        updateState {
            copy(
                searchQuery = query,
                filteredServices = filtered
            )
        }
    }
}
