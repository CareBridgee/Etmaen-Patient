package com.carenest.presentation.ui.search_for_nurse

import com.carenest.domain.model.PaymentMethod

data class NearbyNurse(
    val id: String,
    val name: String,
    val title: String, // "RN", "NP"
    val price: Double,
    val rating: Double,
    val reviewCount: Int,
    val area: String,
    val distanceKm: Double,
    val avatarUrl: String? = null
)

data class NurseSearchState(
    val nearbyNurses: List<NearbyNurse> = emptyList(),
    val activeNursesCount: Int = 0,
    val isSearching: Boolean = true,
    val matchedNurseId: String? = null,
    val showPaymentSheet: Boolean = false,
    val selectedNurseIdForPayment: String? = null,
    val selectedPaymentMethod: PaymentMethod = PaymentMethod.COD,
    val paymentMethods: List<PaymentMethod> = listOf(
        PaymentMethod.COD,
        PaymentMethod.PAYMOB
    )
)

sealed interface NurseSearchIntent {
    data object StartSearching : NurseSearchIntent
    data class AcceptOffer(val nurseId: String) : NurseSearchIntent
    data class DeclineOffer(val nurseId: String) : NurseSearchIntent
    data object CancelSearch : NurseSearchIntent
    data class PaymentMethodSelected(val paymentMethod: PaymentMethod) : NurseSearchIntent
    data object ConfirmPayment : NurseSearchIntent
    data object DismissPaymentSheet : NurseSearchIntent
}

sealed interface NurseSearchEffect {
    data class NavigateToEnRoute(val nurseId: String) : NurseSearchEffect
    data object NavigateBack : NurseSearchEffect
}