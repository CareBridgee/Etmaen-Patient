package com.carenest.presentation.ui.request_service

import com.carenest.domain.model.home.HealthcareService
import com.carenest.domain.model.LocationDetails
import com.carenest.domain.model.Patient
import com.carenest.domain.model.PaymentMethod

data class RequestServiceUiState(
    val patients: List<Patient> = emptyList(),
    val selectedPatient: Patient? = null,
    val selectedService: HealthcareService? = null,
    val description: String = "",
    val location: LocationDetails? = null,
    val paymentMethods: List<PaymentMethod> = emptyList(),
    val selectedPaymentMethod: PaymentMethod? = null,
    val isSubmitting: Boolean = false,
    val isLoading: Boolean = false,
    val isListening: Boolean = false,
    val error: String? = null,
)

sealed class RequestServiceIntent {
    data class OnPatientSelected(val patient: Patient) : RequestServiceIntent()
    data object OnEditProfileClicked : RequestServiceIntent()
    data object OnAddPatientClicked : RequestServiceIntent()
    data object OnChangeServiceClicked : RequestServiceIntent()
    data class OnDescriptionChanged(val description: String) : RequestServiceIntent()
    data object OnEditAddressClicked : RequestServiceIntent()
    data object OnMapClicked : RequestServiceIntent()
    data class OnPaymentMethodSelected(val paymentMethod: PaymentMethod) : RequestServiceIntent()
    data object OnSubmitClicked : RequestServiceIntent()
    data object OnBackClicked : RequestServiceIntent()
    data object OnHelpClicked : RequestServiceIntent()
    data object OnFillWithAiClicked : RequestServiceIntent()
}

sealed class RequestServiceEffect {
    data object NavigateBack : RequestServiceEffect()
    data class ShowError(val message: String) : RequestServiceEffect()
    data object NavigateToEditProfile : RequestServiceEffect()
    data object NavigateToAddPatient : RequestServiceEffect()
    data object NavigateToServiceSelection : RequestServiceEffect()
    data object NavigateToAddressPicker : RequestServiceEffect()
    data object NavigateToMap : RequestServiceEffect()
    data object RequestSubmittedSuccessfully : RequestServiceEffect()
}
