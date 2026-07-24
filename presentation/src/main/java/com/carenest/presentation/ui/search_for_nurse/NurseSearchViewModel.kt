package com.carenest.presentation.ui.search_for_nurse

import androidx.lifecycle.ViewModel
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NurseSearchViewModel @Inject constructor(
) : ViewModel(), StateHolder<NurseSearchState> by DefaultStateHolder(NurseSearchState()),
    EffectPublisher<NurseSearchEffect> by DefaultEffectPublisher() {


    init {
        onIntent(NurseSearchIntent.StartSearching)
    }

    fun onIntent(intent: NurseSearchIntent) {
        when (intent) {
            NurseSearchIntent.StartSearching -> {}
            is NurseSearchIntent.AcceptOffer -> acceptOffer(intent.nurseId)
            is NurseSearchIntent.DeclineOffer -> declineOffer(intent.nurseId)
            NurseSearchIntent.CancelSearch -> {
                sendEffect(NurseSearchEffect.NavigateBack)
            }
            is NurseSearchIntent.PaymentMethodSelected -> {
                updateState {
                    copy(paymentMethods = paymentMethods.map {
                        it.copy(isSelected = it.id == intent.paymentMethod.id)
                    })
                }
            }
            NurseSearchIntent.ConfirmPayment -> {
                state.value.selectedNurseIdForPayment?.let { nurseId ->
                    updateState { copy(showPaymentSheet = false, matchedNurseId = nurseId, isSearching = true) }
                    sendEffect(NurseSearchEffect.NavigateToEnRoute(nurseId))
                }
            }
            NurseSearchIntent.DismissPaymentSheet -> {
                updateState { copy(showPaymentSheet = false, selectedNurseIdForPayment = null) }
            }
        }
    }

    private fun observeNurses() {

    }

    private fun acceptOffer(nurseId: String) {
        updateState { copy(showPaymentSheet = true, selectedNurseIdForPayment = nurseId) }
    }


    private fun declineOffer(nurseId: String) {

    }
}
