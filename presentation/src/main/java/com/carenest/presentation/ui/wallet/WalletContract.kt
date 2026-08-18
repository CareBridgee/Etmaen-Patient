package com.carenest.presentation.ui.wallet

import androidx.annotation.StringRes
import com.carenest.domain.model.payment.WalletTopUpAmount
import com.carenest.presentation.R

data class WalletState(
    val balanceState: WalletBalanceState = WalletBalanceState.Loading,
    val topUpAmount: String = "",
    val isAddingFunds: Boolean = false,
    val isRetryingCreditAdd: Boolean = false,
    val hasPendingCreditAdd: Boolean = false,
    val topUpAmountError: Boolean = false,
) {
    val parsedTopUpAmount: WalletTopUpAmount? get() =
        WalletTopUpAmount.parse(topUpAmount).getOrNull()
    val isTopUpAmountValid: Boolean get() = parsedTopUpAmount != null
    val canAddFunds: Boolean get() = isTopUpAmountValid && !isAddingFunds && !hasPendingCreditAdd
}

sealed interface WalletIntent {
    data object LoadWallet : WalletIntent
    data object RefreshBalance : WalletIntent
    data class TopUpAmountChanged(val value: String) : WalletIntent
    data object AddFundsClicked : WalletIntent
    data object RetryPendingCreditAdd : WalletIntent
    data object ErrorShown : WalletIntent
}

sealed interface WalletBalanceState {
    data object Loading : WalletBalanceState
    data object Empty : WalletBalanceState
    data class Available(val credit: Double) : WalletBalanceState
    data class Failure(@param:StringRes val messageRes: Int = R.string.wallet_balance_load_failed) : WalletBalanceState
}

sealed interface WalletEffect {
    data class ShowMessage(@param:StringRes val messageRes: Int) : WalletEffect
    data class ShowTextMessage(val message: String) : WalletEffect
}
