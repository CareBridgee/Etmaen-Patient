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
            preferredDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date()),
            preferredHour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY),
            preferredMinute = java.util.Calendar.getInstance().get(java.util.Calendar.MINUTE),
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

            RequestServiceIntent.OnSubmitClicked -> submitServiceRequest()

            RequestServiceIntent.OnBackClicked -> {
                sendEffect(RequestServiceEffect.NavigateBack)
            }

            RequestServiceIntent.OnMapClicked -> {
                sendEffect(RequestServiceEffect.NavigateToMap)
            }

            is RequestServiceIntent.OnLocationDetailsReceived -> {
                updateState { copy(location = intent.locationDetails) }
            }
            is RequestServiceIntent.OnPreferredDateChanged -> {
                updateState { copy(preferredDate = intent.date) }
            }
            is RequestServiceIntent.OnPreferredTimeChanged -> {
                updateState { copy(preferredHour = intent.hour, preferredMinute = intent.minute) }
            }
        }
    }

    private fun submitServiceRequest() {
        val currentState = state.value
        val serviceId = currentState.selectedService?.id
        val profileId = currentState.selectedPatient?.defaultProfileId
        val location = currentState.location

        if (serviceId == null || profileId == null || location == null) {
            sendEffect(RequestServiceEffect.ShowError("Please fill all required fields"))
            return
        }

        if (currentState.preferredDate.isBlank()) {
            sendEffect(RequestServiceEffect.ShowError("Please select a preferred date"))
            return
        }

        updateState { copy(isSubmitting = true) }
        viewModelScope.launch {
            homeRepository.submitServiceRequest(
                com.carenest.domain.model.CreateServiceRequestParams(
                    profileId = profileId,
                    serviceTypeId = serviceId,
                    latitude = location.latitude,
                    longitude = location.longitude,
                    preferredDate = currentState.preferredDate,
                    preferredTime = com.carenest.domain.model.PreferredTime(
                        hour = currentState.preferredHour,
                        minute = currentState.preferredMinute,
                    ),
                    serviceDescription = currentState.description,
                )
            ).onSuccess { result ->
                updateState { copy(isSubmitting = false) }
                sendEffect(
                    RequestServiceEffect.RequestSubmittedSuccessfully(
                        serviceRequestId = result.serviceRequestId
                    )
                )
            }.onFailure { error ->
                updateState { copy(isSubmitting = false) }
                sendEffect(RequestServiceEffect.ShowError(error.message ?: "Failed to submit request"))
            }
        }
    }
}
