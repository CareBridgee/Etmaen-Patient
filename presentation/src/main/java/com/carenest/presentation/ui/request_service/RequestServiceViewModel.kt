package com.carenest.presentation.ui.request_service

import androidx.lifecycle.ViewModel
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import com.carenest.presentation.model.toDomainModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

import com.carenest.domain.repository.HomeRepository
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

@HiltViewModel
class RequestServiceViewModel @Inject constructor(
    private val homeRepository: HomeRepository
): ViewModel(),
    StateHolder<RequestServiceUiState> by DefaultStateHolder(
        RequestServiceUiState(
            location = com.carenest.domain.model.LocationDetails(
                address = "Cairo, Egypt",
                apartment = "",
                district = "",
                latitude = 30.0444,
                longitude = 31.2357
            )
        )
    ),
    EffectPublisher<RequestServiceEffect> by DefaultEffectPublisher() {

    fun onIntent(intent: RequestServiceIntent) {
        when (intent) {
            is RequestServiceIntent.OnStart -> {
                intent.serviceId?.let { id ->
                    viewModelScope.launch {
                        homeRepository.getServiceDetails(id).onSuccess { serviceDetails ->
                            val healthcareService = com.carenest.domain.model.home.HealthcareService(
                                id = serviceDetails.id,
                                name = serviceDetails.name,
                                estimatedDurationMinutes = serviceDetails.estimatedDurationMinutes.toLong(),
                                basePrice = serviceDetails.basePrice,
                                description = serviceDetails.description,
                                iconResName = null
                            )
                            updateState { copy(selectedService = healthcareService) }
                        }
                    }
                }
            }

            is RequestServiceIntent.OnDescriptionChanged -> {
                updateState { copy(description = intent.description, isListening = false) }
            }

            is RequestServiceIntent.OnPatientSelected -> {
                updateState { copy(selectedPatient = intent.patient) }
            }

            is RequestServiceIntent.OnPaymentMethodSelected -> {
                updateState { copy(selectedPaymentMethod = intent.paymentMethod) }
            }

            RequestServiceIntent.OnAddPatientClicked -> {
                sendEffect(RequestServiceEffect.NavigateToAddPatient)
            }

            RequestServiceIntent.OnChangeServiceClicked -> {
                sendEffect(RequestServiceEffect.NavigateToServiceSelection(currentState.selectedService?.id))
            }

            RequestServiceIntent.OnEditAddressClicked -> {
                sendEffect(RequestServiceEffect.NavigateToAddressPicker)
            }

            RequestServiceIntent.OnEditProfileClicked -> {
                sendEffect(RequestServiceEffect.NavigateToEditProfile)
            }

            RequestServiceIntent.OnFillWithAiClicked -> {
                // Implement AI fill logic if needed
            }

            RequestServiceIntent.OnHelpClicked -> {
                // Implement help logic if needed
            }

            RequestServiceIntent.OnSubmitClicked -> {
                sendEffect(RequestServiceEffect.RequestSubmittedSuccessfully)
            }

            RequestServiceIntent.OnBackClicked -> {
                sendEffect(RequestServiceEffect.NavigateBack)
            }

            RequestServiceIntent.OnMapClicked -> {
                sendEffect(RequestServiceEffect.NavigateToMap)
            }

            is RequestServiceIntent.OnLocationDetailsReceived -> {
                updateState { copy(location = intent.locationDetails) }
            }
        }
    }
}
