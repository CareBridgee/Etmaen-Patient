package com.carenest.presentation.ui.search_for_nurse

import com.carenest.domain.model.PaymentMethod
import com.carenest.domain.socket.model.NurseOfferResponse

data class NurseSearchState(
    val offers: List<NurseOfferResponse> = emptyList(),
    val activeNursesCount: Int = 0,
    val isSearching: Boolean = true,
    val showPaymentSheet: Boolean = false,
    val selectedOfferForPayment: NurseOfferResponse? = null,
    val selectedPaymentMethod: PaymentMethod = PaymentMethod.COD,
    val showCancelConfirmation: Boolean = false,
    val paymentMethods: List<PaymentMethod> = listOf(
        PaymentMethod.COD,
        PaymentMethod.PAYMOB
    )
)

sealed interface NurseSearchIntent {
    data class StartSearching(val reservationId: String, val serviceRequestId: String) : NurseSearchIntent
    data class AcceptOffer(val offerId: String) : NurseSearchIntent
    data class DeclineOffer(val offerId: String) : NurseSearchIntent
    data object CancelSearch : NurseSearchIntent
    data object ConfirmCancelSearch : NurseSearchIntent
    data object DismissCancelConfirmation : NurseSearchIntent
    data class PaymentMethodSelected(val paymentMethod: PaymentMethod) : NurseSearchIntent
    data object ConfirmPayment : NurseSearchIntent
    data object DismissPaymentSheet : NurseSearchIntent
}

sealed interface NurseSearchEffect {
    data class NavigateToEnRoute(val requestId: String) : NurseSearchEffect
    data object NavigateBack : NurseSearchEffect
    data class ShowError(val message: String) : NurseSearchEffect
}