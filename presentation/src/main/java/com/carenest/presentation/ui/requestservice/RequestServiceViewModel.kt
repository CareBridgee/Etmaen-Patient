package com.carenest.presentation.ui.requestservice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.domain.model.PreferredTime
import com.carenest.domain.model.payment.ServicePaymentMethod
import com.carenest.domain.model.payment.WalletException
import com.carenest.domain.repository.HomeRepository
import com.carenest.domain.repository.ProfileRepository
import com.carenest.domain.model.profile.PersonalInfoUpdate
import com.carenest.domain.usecase.user.ObserveCurrentUserUseCase
import com.carenest.domain.usecase.wallet.GetWalletCreditUseCase
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
    private val getWalletCreditUseCase: GetWalletCreditUseCase,
    private val aiChatRepository: com.carenest.domain.repository.AiChatRepository
) : ViewModel(),
    StateHolder<RequestServiceUiState> by DefaultStateHolder(
        RequestServiceUiState(
            preferredDate = SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.US
            ).format(Date()), preferredHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
            preferredMinute = Calendar.getInstance()
                .get(Calendar.MINUTE),
        )
    ),
    EffectPublisher<RequestServiceEffect> by DefaultEffectPublisher() {

    init {
        viewModelScope.launch {
            observeCurrentUserUseCase().collect { user ->
                user ?: return@collect

                val defaultProfileId = user.defaultProfileId ?: return@collect

                updateState {
                    copy(
                        patients = patients.map { patient ->
                            val patientProfileId =
                                patient.defaultProfileId ?: patient.id

                            if (patientProfileId == defaultProfileId) {
                                patient.copy(
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
                            } else {
                                patient
                            }
                        },

                        selectedPatient = selectedPatient?.let { selected ->
                            val selectedProfileId =
                                selected.defaultProfileId ?: selected.id

                            if (selectedProfileId == defaultProfileId) {
                                selected.copy(
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
                            } else {
                                selected
                            }
                        }
                    )
                }
            }
        }
    }

    fun onIntent(intent: RequestServiceIntent) {
        when (intent) {
            is RequestServiceIntent.OnStart -> {
                viewModelScope.launch {
                    updateState { copy(isLoading = true, error = null) }
                    val currentUser = homeRepository.getUser().getOrNull()
                    // Fetch all available profiles (family members + self)
                    profileRepository.getProfiles().onSuccess { profiles ->

                        val mappedPatients = profiles.map { profile ->
                            val isSelf = profile.isPrimary || profile.id == currentUser?.defaultProfileId

                            if (isSelf && currentUser != null) {
                                // Self profile:
                                // use current user account data
                                com.carenest.domain.model.Patient(
                                    id = profile.id,
                                    phoneNumber = currentUser.phoneNumber,
                                    firstName = currentUser.firstName,
                                    lastName = currentUser.lastName,
                                    dateOfBirth = currentUser.dateOfBirth
                                        ?: profile.dateOfBirth,
                                    gender = currentUser.gender
                                        ?: profile.gender,
                                    profileImageUrl = currentUser.profileImageUrl
                                        ?: profile.profileImageUrl,
                                    isDeleted = currentUser.isDeleted,
                                    createdAt = currentUser.createdAt.orEmpty(),
                                    updatedAt = currentUser.updatedAt.orEmpty(),
                                    lastLoginAt = currentUser.lastLoginAt,
                                    defaultProfileId = profile.id
                                )

                            } else {

                                com.carenest.domain.model.Patient(
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
                                    defaultProfileId = profile.id
                                )
                            }
                        }

                        updateState { copy(patients = mappedPatients) }

                        // If no patient is selected yet, select primary/default profile first
                        if (currentState.selectedPatient == null) {

                            val primaryProfile = profiles.find { it.isPrimary || it.id == currentUser?.defaultProfileId }
                            val primaryPatient = primaryProfile?.let { profile -> mappedPatients.find { it.id == profile.id }
                            }
                            updateState { copy(selectedPatient = primaryPatient ?: mappedPatients.firstOrNull()) }
                        }
                    }

                    intent.serviceId?.let { id ->
                        homeRepository.getServiceDetails(id)
                            .onSuccess { serviceDetails ->

                                val healthcareService =
                                    com.carenest.domain.model.home.HealthcareService(
                                        id = serviceDetails.id,
                                        name = serviceDetails.name,
                                        estimatedDurationMinutes =
                                            serviceDetails
                                                .estimatedDurationMinutes
                                                .toLong(),
                                        basePrice = serviceDetails.basePrice,
                                        description = serviceDetails.description,
                                        iconResName = null
                                    )

                                updateState {
                                    copy(
                                        selectedService = healthcareService
                                    )
                                }
                            }
                    }
                    updateState { copy(isLoading = false) }
                }
            }

            is RequestServiceIntent.OnDescriptionChanged -> {
                updateState {
                    copy(
                        description = intent.description,
                        isListening = false
                    )
                }
            }

            is RequestServiceIntent.OnPatientSelected -> { updateState { copy(selectedPatient = intent.patient) } }
            is RequestServiceIntent.OnPaymentMethodSelected -> {
                updateState { copy(selectedPaymentMethod = intent.paymentMethod) }
                if (intent.paymentMethod == ServicePaymentMethod.Wallet) {
                    loadWalletCredit()
                }
            }
            RequestServiceIntent.OnWalletCreditRetryClicked -> { loadWalletCredit(force = true) }
            RequestServiceIntent.OnAddWalletCreditClicked -> { sendEffect(RequestServiceEffect.NavigateToAddFunds) }
            RequestServiceIntent.OnWalletCashRemainderConfirmed -> {
                updateState { copy(walletCashRemainderAlert = null) }
                submitServiceRequest(allowWalletCashRemainder = true)
            }
            RequestServiceIntent.OnWalletCashRemainderDismissed -> {
                updateState { copy(walletCashRemainderAlert = null) }
            }
            RequestServiceIntent.OnAddPatientClicked -> { sendEffect(RequestServiceEffect.NavigateToAddPatient) }
            RequestServiceIntent.OnChangeServiceClicked -> { sendEffect(RequestServiceEffect.NavigateToServiceSelection(currentState.selectedService?.id)) }
            RequestServiceIntent.OnEditAddressClicked -> { sendEffect(RequestServiceEffect.NavigateToAddressPicker) }
            RequestServiceIntent.OnEditProfileClicked -> { sendEffect(RequestServiceEffect.NavigateToEditProfile) }
            RequestServiceIntent.OnFillWithAiClicked -> {
                val aiReport = aiChatRepository.getLastAiReport().orEmpty()
                updateState { copy(description = aiReport) }
            }

            RequestServiceIntent.OnSubmitClicked -> { submitServiceRequest() }
            RequestServiceIntent.OnBackClicked -> { sendEffect(RequestServiceEffect.NavigateBack) }
            RequestServiceIntent.OnMapClicked -> { sendEffect(RequestServiceEffect.NavigateToMap(currentState.location)) }
            is RequestServiceIntent.OnLocationDetailsReceived -> { updateState { copy(location = intent.locationDetails) } }
            is RequestServiceIntent.OnPreferredDateChanged -> { updateState { copy(preferredDate = intent.date) } }
            is RequestServiceIntent.OnPreferredTimeChanged -> {
                updateState {
                    copy(
                        preferredHour = intent.hour,
                        preferredMinute = intent.minute
                    )
                }
            }
        }
    }

    private fun submitServiceRequest(allowWalletCashRemainder: Boolean = false) {
        val currentState = state.value
        if (currentState.isSubmitting) return

        val serviceId = currentState.selectedService?.id
        val selectedPatient = currentState.selectedPatient
        val profileId = selectedPatient?.defaultProfileId ?: selectedPatient?.id

        val location = currentState.location

        if (
            serviceId == null ||
            location == null ||
            profileId == null
        ) {
            sendEffect(
                RequestServiceEffect.ShowError(
                    RequestServiceUiError.RequiredFields.messageRes
                )
            )
            return
        }

        if (currentState.preferredDate.isBlank()) {
            sendEffect(RequestServiceEffect.ShowError(RequestServiceUiError.PreferredDate.messageRes))
            return
        }

        val patient = selectedPatient ?: return
        if (!allowWalletCashRemainder) {
            val cashRemainderAlert = currentState.walletCashRemainderAlert()
            if (cashRemainderAlert != null) {
                updateState { copy(walletCashRemainderAlert = cashRemainderAlert) }
                return
            }
        }

        updateState { copy(isSubmitting = true) }
        viewModelScope.launch {
            val currentUser = homeRepository.getUser().getOrNull()
            val isPrimaryPatient = currentUser?.defaultProfileId == profileId

            if (isPrimaryPatient) {
                val primaryProfileUpdate = PersonalInfoUpdate(
                    firstName = patient.firstName.orEmpty(),
                    lastName = patient.lastName.orEmpty(),
                    dateOfBirth = patient.dateOfBirth.orEmpty(),
                    gender = patient.gender.orEmpty(),
                    profileImageUrl = patient.profileImageUrl,
                )
                profileRepository.updatePersonalInfo(profileId, primaryProfileUpdate)
                    .onFailure {
                        updateState { copy(isSubmitting = false) }
                        sendEffect(
                            RequestServiceEffect.ShowError(
                                RequestServiceUiError.ProfileSync.messageRes
                            )
                        )
                        return@launch
                    }
            }

            homeRepository.submitServiceRequest(
                com.carenest.domain.model.CreateServiceRequestParams(
                    profileId = profileId,
                    serviceTypeId = serviceId,
                    latitude = location.latitude,
                    longitude = location.longitude,
                    address = location.address,
                    district = location.district.ifBlank { location.address },
                    apartment = location.apartment,
                    preferredDate = currentState.preferredDate,
                    preferredTime =
                        PreferredTime(
                            hour = currentState.preferredHour,
                            minute = currentState.preferredMinute,
                            second = 0,
                            nano = 0
                        ),
                    serviceDescription =
                        currentState.description.ifBlank {
                            "No description provided"
                        },
                    paymentType = currentState.selectedPaymentMethod.paymentType,
                )
            ).onSuccess { result ->
                updateState { copy(isSubmitting = false) }
                sendEffect(
                    RequestServiceEffect.RequestSubmittedSuccessfully(
                        serviceRequestId = result.serviceRequestId
                    )
                )
            }.onFailure {
                updateState { copy(isSubmitting = false) }
                sendEffect(RequestServiceEffect.ShowError(RequestServiceUiError.Submit.messageRes))
            }
        }
    }

    private fun loadWalletCredit(force: Boolean = false) {
        val walletState = currentState.walletCreditState
        if (!force && walletState is WalletCreditUiState.Available) return
        if (!force && walletState is WalletCreditUiState.Empty) return
        if (walletState is WalletCreditUiState.Loading) return

        updateState { copy(walletCreditState = WalletCreditUiState.Loading) }

        viewModelScope.launch {
            getWalletCreditUseCase()
                .onSuccess { credit ->
                    updateState {
                        copy(
                            walletCreditState = if (credit.credit > 0.0) {
                                WalletCreditUiState.Available(credit.credit)
                            } else {
                                WalletCreditUiState.Empty
                            }
                        )
                    }
                }
                .onFailure { error ->
                    updateState {
                        copy(
                            walletCreditState = WalletCreditUiState.Failure(
                                walletErrorRes(error)
                            )
                        )
                    }
                }
        }
    }

    private fun walletErrorRes(error: Throwable): Int =
        when (error) {
            WalletException.MissingAuthenticatedUserId ->
                RequestServiceUiError.RequiredFields.messageRes
            else -> RequestServiceUiError.WalletCredit.messageRes
        }

    private fun RequestServiceUiState.walletCashRemainderAlert(): WalletCashRemainderAlert? {
        if (selectedPaymentMethod != ServicePaymentMethod.Wallet) return null
        val servicePrice = selectedService?.basePrice ?: return null
        val walletCredit = (walletCreditState as? WalletCreditUiState.Available)?.credit ?: return null
        if (walletCredit <= 0.0 || walletCredit >= servicePrice) return null

        return WalletCashRemainderAlert(
            walletCredit = walletCredit,
            cashRemainder = servicePrice - walletCredit,
        )
    }
}
