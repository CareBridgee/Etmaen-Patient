package com.carenest.presentation.ui.wallet

enum class WalletPaymentMethod { CASH, CARD, PAYPAL, FAWRY_CASH, MEEZA_CARD, MOBILE_WALLET }

data class WalletState(
    val balance: String = "0.00",
    val isAutoRefillEnabled: Boolean = false,
    val selectedPaymentMethod: WalletPaymentMethod? = null,
    val topUpAmount: String = "",
) {
    val parsedTopUpAmount: Double? get() = topUpAmount.toDoubleOrNull()
    val isTopUpAmountValid: Boolean get() = parsedTopUpAmount?.let { it in 120.0..1240.0 } == true
    val canAddFunds: Boolean get() = isTopUpAmountValid && selectedPaymentMethod != null
}

sealed interface WalletIntent {
    data class TopUpAmountChanged(val value: String) : WalletIntent
    data class SuggestedAmountSelected(val amount: Int) : WalletIntent
    data class PaymentMethodSelected(val method: WalletPaymentMethod) : WalletIntent
}
