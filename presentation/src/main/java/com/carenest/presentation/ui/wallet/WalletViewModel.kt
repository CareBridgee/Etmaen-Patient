package com.carenest.presentation.ui.wallet

import androidx.lifecycle.ViewModel
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor() : ViewModel(),
    StateHolder<WalletState> by DefaultStateHolder(WalletState()) {

    fun onEvent(intent: WalletIntent) {
        when (intent) {
            is WalletIntent.TopUpAmountChanged -> updateState {
                copy(topUpAmount = intent.value.filter { it.isDigit() || it == '.' })
            }

            is WalletIntent.SuggestedAmountSelected -> updateState {
                copy(topUpAmount = intent.amount.toString())
            }

            is WalletIntent.PaymentMethodSelected -> updateState {
                copy(selectedPaymentMethod = intent.method)
            }
        }
    }
}
