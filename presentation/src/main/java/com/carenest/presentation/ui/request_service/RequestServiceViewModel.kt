package com.carenest.presentation.ui.request_service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.domain.model.CreateServiceRequestParams
import com.carenest.domain.model.Patient
import com.carenest.domain.model.PreferredTime
import com.carenest.domain.model.home.HealthcareService
import com.carenest.domain.repository.HomeRepository
import com.carenest.domain.repository.ProfileRepository
import com.carenest.domain.usecase.user.ObserveCurrentUserUseCase
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class RequestServiceViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val profileRepository: ProfileRepository,
    private val observeCurrentUserUseCase: ObserveCurrentUserUseCase,
) : ViewModel(), StateHolder<RequestServiceUiState> by DefaultStateHolder(
    RequestServiceUiState(
        preferredDate = SimpleDateFormat(
            "yyyy-MM-dd", Locale.US
        ).format(Date()),
        preferredHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
        preferredMinute = Calendar.getInstance().get(Calendar.MINUTE),
    )
), EffectPublisher<RequestServiceEffect> by DefaultEffectPublisher() {

    init {
        observeCurrentUser()
    }

    private fun observeCurrentUser() {
        viewModelScope.launch {
            observeCurrentUserUseCase().collect { user ->

                val selectedPatient = currentState.selectedPatient ?: return@collect

                val selectedProfileId = selectedPatient.defaultProfileId ?: selectedPatient.id

                // Only update the patient if it represents
                // the currently logged-in user's default profile.
                if (selectedProfileId != user?.defaultProfileId) {
                    return@collect
                }

                updateState {
                    copy(
                        selectedPatient = selectedPatient.copy(
                            phoneNumber = user.phoneNumber,
                            firstName = user.firstName,
                            lastName = user.lastName,
                            dateOfBirth = user.dateOfBirth,
                            gender = user.gender,
                            profileImageUrl = user.profileImageUrl,
                            isDeleted = user.isDeleted,
                            createdAt = user.createdAt.orEmpty(),
                            updatedAt = user.updatedAt.orEmpty(),
                            lastLoginAt = user.lastLoginAt,
                        )
                    )
                }
            }
        }
    }

    fun onIntent(intent: RequestServiceIntent) {
        when (intent) {
            is RequestServiceIntent.OnStart -> {
                viewModelScope.launch {

                    profileRepository.getProfiles().onSuccess { profiles ->

                            val mappedPatients = profiles.map { profile ->
                                Patient(
                                    id = profile.id,
                                    phoneNumber = "",
                                    firstName = profile.firstName,
                                    lastName = profile.lastName,
                                    dateOfBirth = profile.dateOfBirth,
                                    gender = profile.gender,
                                    profileImageUrl = profile.profileImageUrl,
                                    isDeleted = profile.isDeleted,
                                    createdAt = "",
                                    updatedAt = "",
                                    lastLoginAt = null,
                                    defaultProfileId = profile.id,
                                )
                            }

                            updateState { copy(patients = mappedPatients) }

                            if (currentState.selectedPatient == null) {

                                val selectedPatient =
                                    profiles.find { it.isPrimary }?.let { primaryProfile ->
                                            mappedPatients.find {
                                                it.id == primaryProfile.id
                                            }
                                        } ?: mappedPatients.firstOrNull()

                                selectedPatient?.let { patient ->
                                    updateState {
                                        copy(
                                            selectedPatient = patient
                                        )
                                    }
                                }
                            }
                        }

                    intent.serviceId?.let { serviceId ->

                        homeRepository.getServiceDetails(serviceId).onSuccess { serviceDetails ->

                                val healthcareService = HealthcareService(
                                    id = serviceDetails.id,
                                    name = serviceDetails.name,
                                    estimatedDurationMinutes = serviceDetails.estimatedDurationMinutes.toLong(),
                                    basePrice = serviceDetails.basePrice,
                                    description = serviceDetails.description,
                                    iconResName = null,
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
                updateState {
                    copy(
                        location = intent.locationDetails
                    )
                }
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
        val selectedPatient = currentState.selectedPatient
        val profileId = selectedPatient?.defaultProfileId ?: selectedPatient?.id
        val location = currentState.location

        if (serviceId == null || location == null || profileId == null) {
            sendEffect(RequestServiceEffect.ShowError("Please fill all required fields and select a patient"))
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
                        second = 0,
                        nano = 0
                    ),
                    serviceDescription = currentState.description.ifBlank { "No description provided" },
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