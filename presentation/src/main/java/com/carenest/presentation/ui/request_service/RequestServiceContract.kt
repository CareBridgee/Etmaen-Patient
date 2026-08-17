package com.carenest.presentation.ui.request_service

import androidx.annotation.StringRes
import com.carenest.designsystem.R
import com.carenest.domain.model.home.HealthcareService
import com.carenest.domain.model.LocationDetails
import com.carenest.domain.model.Patient
import com.carenest.domain.model.payment.ServicePaymentMethod

val DEFAULT_CAIRO_LOCATION = LocationDetails(
    address = "Cairo, Egypt",
    apartment = "",
    district = "Cairo Governorate",
    city = "Cairo",
    latitude = 30.0444,
    longitude = 31.2357,
)

data class RequestServiceUiState(
    val patients: List<Patient> = emptyList(),
    val selectedPatient: Patient? = null,
    val selectedService: HealthcareService? = null,
    val description: String = "",
    val location: LocationDetails? = DEFAULT_CAIRO_LOCATION,
    val preferredDate: String = "",          // "yyyy-MM-dd"
    val preferredHour: Int = 9,
    val preferredMinute: Int = 0,
    val paymentMethods: List<ServicePaymentMethod> = ServicePaymentMethod.entries,
    val selectedPaymentMethod: ServicePaymentMethod = ServicePaymentMethod.Cash,
    val walletCreditState: WalletCreditUiState = WalletCreditUiState.NotRequested,
    val walletCashRemainderAlert: WalletCashRemainderAlert? = null,
    val isSubmitting: Boolean = false,
    val isLoading: Boolean = false,
    val isListening: Boolean = false,
    val error: String? = null,
)

sealed class RequestServiceIntent {
    data class OnStart(
        val serviceId: String?,
        val isFromAi: Boolean = false
    ) : RequestServiceIntent()
    data class OnPatientSelected(val patient: Patient) : RequestServiceIntent()
    data object OnEditProfileClicked : RequestServiceIntent()
    data object OnAddPatientClicked : RequestServiceIntent()
    data object OnChangeServiceClicked : RequestServiceIntent()
    data class OnDescriptionChanged(val description: String) : RequestServiceIntent()
    data object OnEditAddressClicked : RequestServiceIntent()
    data object OnMapClicked : RequestServiceIntent()
    data class OnPaymentMethodSelected(val paymentMethod: ServicePaymentMethod) : RequestServiceIntent()
    data object OnWalletCreditRetryClicked : RequestServiceIntent()
    data object OnAddWalletCreditClicked : RequestServiceIntent()
    data object OnWalletCashRemainderConfirmed : RequestServiceIntent()
    data object OnWalletCashRemainderDismissed : RequestServiceIntent()
    data object OnSubmitClicked : RequestServiceIntent()
    data object OnBackClicked : RequestServiceIntent()
    data object OnFillWithAiClicked : RequestServiceIntent()
    data class OnLocationDetailsReceived(val locationDetails: LocationDetails) : RequestServiceIntent()
    data class OnPreferredDateChanged(val date: String) : RequestServiceIntent()
    data class OnPreferredTimeChanged(val hour: Int, val minute: Int) : RequestServiceIntent()
}

sealed class RequestServiceEffect {
    data object NavigateBack : RequestServiceEffect()
    data class ShowError(@param:StringRes val messageRes: Int) : RequestServiceEffect()
    data object NavigateToEditProfile : RequestServiceEffect()
    data object NavigateToAddPatient : RequestServiceEffect()
    data class NavigateToServiceSelection(val currentServiceId: String?) : RequestServiceEffect()
    data object NavigateToAddressPicker : RequestServiceEffect()
    data class NavigateToMap(val location: LocationDetails? = null) : RequestServiceEffect()
    data object NavigateToAddFunds : RequestServiceEffect()
    data class RequestSubmittedSuccessfully(
        val serviceRequestId: String,
    ) : RequestServiceEffect()
}

data class WalletCashRemainderAlert(
    val walletCredit: Double,
    val cashRemainder: Double,
)

sealed interface WalletCreditUiState {
    data object NotRequested : WalletCreditUiState
    data object Loading : WalletCreditUiState
    data object Empty : WalletCreditUiState
    data class Available(val credit: Double) : WalletCreditUiState
    data class Failure(@param:StringRes val messageRes: Int) : WalletCreditUiState
}

enum class RequestServiceUiError(@get:StringRes val messageRes: Int) {
    RequiredFields(R.string.request_service_error_required_fields),
    PreferredDate(R.string.request_service_error_preferred_date),
    ProfileSync(R.string.request_service_error_profile_sync),
    Submit(R.string.request_service_error_submit),
    WalletCredit(R.string.request_service_error_wallet_credit),
}
