package com.carenest.presentation.ui.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.domain.model.payment.WalletException
import com.carenest.domain.model.payment.WalletTopUpAmount
import com.carenest.domain.model.payment.WalletTopUpPaymentResult
import com.carenest.domain.repository.WalletTopUpPaymentGateway
import com.carenest.domain.usecase.wallet.AddWalletCreditAfterPaymentUseCase
import com.carenest.domain.usecase.wallet.GetPendingWalletTopUpAttemptUseCase
import com.carenest.domain.usecase.wallet.GetWalletCreditUseCase
import com.carenest.domain.usecase.wallet.RetryPendingWalletTopUpCreditUseCase
import com.carenest.presentation.R
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val getWalletCreditUseCase: GetWalletCreditUseCase,
    private val walletTopUpPaymentGateway: WalletTopUpPaymentGateway,
    private val addWalletCreditAfterPaymentUseCase: AddWalletCreditAfterPaymentUseCase,
    private val getPendingWalletTopUpAttemptUseCase: GetPendingWalletTopUpAttemptUseCase,
    private val retryPendingWalletTopUpCreditUseCase: RetryPendingWalletTopUpCreditUseCase,
) : ViewModel(),
    StateHolder<WalletState> by DefaultStateHolder(WalletState()),
    EffectPublisher<WalletEffect> by DefaultEffectPublisher() {

    init {
        loadCredit()
        loadPendingCreditAdd()
    }

    fun onEvent(intent: WalletIntent) {
        when (intent) {
            WalletIntent.LoadWallet -> loadCredit()
            WalletIntent.RefreshBalance -> loadCredit(force = true)
            is WalletIntent.TopUpAmountChanged -> updateState {
                copy(
                    topUpAmount = intent.value.filter { it.isDigit() || it == '.' },
                    topUpAmountError = false,
                )
            }

            WalletIntent.AddFundsClicked -> addFunds()
            WalletIntent.RetryPendingCreditAdd -> retryPendingCreditAdd()
            WalletIntent.ErrorShown -> Unit
        }
    }

    private fun loadCredit(force: Boolean = false) {
        if (!force && currentState.balanceState is WalletBalanceState.Available) return
        if (!force && currentState.balanceState is WalletBalanceState.Empty) return

        updateState { copy(balanceState = WalletBalanceState.Loading) }
        viewModelScope.launch {
            getWalletCreditUseCase()
                .onSuccess { credit ->
                    updateState {
                        copy(
                            balanceState = if (credit.credit > 0.0) {
                                WalletBalanceState.Available(credit.credit)
                            } else {
                                WalletBalanceState.Empty
                            }
                        )
                    }
                }
                .onFailure {
                    updateState { copy(balanceState = WalletBalanceState.Failure()) }
                    sendEffect(WalletEffect.ShowMessage(R.string.wallet_balance_load_failed))
                }
        }
    }

    private fun addFunds() {
        val amount = currentState.parsedTopUpAmount
        if (amount == null) {
            updateState { copy(topUpAmountError = true) }
            sendEffect(WalletEffect.ShowMessage(R.string.wallet_error_invalid_amount))
            return
        }

        if (currentState.isAddingFunds) return

        viewModelScope.launch {
            updateState { copy(isAddingFunds = true, topUpAmountError = false) }

            walletTopUpPaymentGateway.startTopUp(amount)
                .fold(
                    onSuccess = { paymentResult -> handlePaymentResult(amount, paymentResult) },
                    onFailure = { error -> handleAddFundsFailure(error) },
                )
        }
    }

    private suspend fun handlePaymentResult(
        amount: WalletTopUpAmount,
        paymentResult: WalletTopUpPaymentResult,
    ) {
        addWalletCreditAfterPaymentUseCase(amount, paymentResult)
            .onSuccess {
                updateState {
                    copy(
                        isAddingFunds = false,
                        topUpAmount = "",
                        balanceState = WalletBalanceState.Available(it.credit),
                    )
                }
                sendEffect(WalletEffect.ShowMessage(R.string.wallet_add_funds_success))
                loadCredit(force = true)
            }
            .onFailure { error ->
                if (paymentResult is WalletTopUpPaymentResult.Success && error.isRetryableCreditAddFailure()) {
                    updateState { copy(isAddingFunds = false, hasPendingCreditAdd = true) }
                    sendEffect(WalletEffect.ShowMessage(R.string.wallet_error_credit_add_pending))
                } else {
                    handleAddFundsFailure(error)
                }
            }
    }

    private fun handleAddFundsFailure(error: Throwable) {
        updateState {
            copy(
                isAddingFunds = false,
                hasPendingCreditAdd = hasPendingCreditAdd || error is WalletException.CreditAddPending,
            )
        }
        sendEffect(error.toWalletEffect())
    }

    private fun retryPendingCreditAdd() {
        if (currentState.isRetryingCreditAdd) return

        viewModelScope.launch {
            updateState { copy(isRetryingCreditAdd = true) }
            retryPendingWalletTopUpCreditUseCase()
                .onSuccess { credit ->
                    updateState {
                        copy(
                            isRetryingCreditAdd = false,
                            hasPendingCreditAdd = false,
                            topUpAmount = "",
                            balanceState = WalletBalanceState.Available(credit.credit),
                        )
                    }
                    sendEffect(WalletEffect.ShowMessage(R.string.wallet_add_funds_success))
                    loadCredit(force = true)
                }
                .onFailure {
                    updateState { copy(isRetryingCreditAdd = false, hasPendingCreditAdd = true) }
                    sendEffect(WalletEffect.ShowMessage(R.string.wallet_error_credit_update_failed))
                }
        }
    }

    private fun loadPendingCreditAdd() {
        viewModelScope.launch {
            val pendingAttempt = getPendingWalletTopUpAttemptUseCase()
            updateState { copy(hasPendingCreditAdd = pendingAttempt != null) }
        }
    }

    private fun Throwable.toMessageRes(): Int =
        when (this) {
            WalletException.InvalidAmount -> R.string.wallet_error_invalid_amount
            WalletException.InsufficientCredit -> R.string.wallet_error_insufficient_credit
            WalletException.MissingAuthenticatedUserId -> R.string.wallet_error_missing_user
            WalletException.PaymentUnavailable -> R.string.wallet_error_paymob_unavailable
            WalletException.PaymentSdkUnavailable -> R.string.wallet_error_paymob_sdk_missing
            WalletException.PaymentConfigurationMissing -> R.string.wallet_error_paymob_config_missing
            WalletException.PaymentIntegrationInvalid -> R.string.wallet_error_paymob_integration_invalid
            WalletException.PaymentCancelled -> R.string.wallet_error_paymob_cancelled
            WalletException.PaymentNotConfirmed -> R.string.wallet_error_paymob_pending
            WalletException.DuplicatePaymentCallback -> R.string.wallet_error_duplicate_payment
            WalletException.CreditAddPending -> R.string.wallet_error_credit_add_pending
            WalletException.PaymentAlreadyInProgress -> R.string.wallet_error_paymob_already_in_progress
            is WalletException.PaymentFailed -> R.string.wallet_error_paymob_failed
            else -> R.string.wallet_error_credit_update_failed
        }

    private fun Throwable.toWalletEffect(): WalletEffect =
        when (this) {
            is WalletException.PaymentFailed -> WalletEffect.ShowTextMessage(
                message
                    .takeIf { it.isNotBlank() }
                    ?.let { "Payment failed: $it. No credit was added." }
                    ?: "Payment failed. No credit was added.",
            )

            else -> WalletEffect.ShowMessage(toMessageRes())
        }

    private fun Throwable.isRetryableCreditAddFailure(): Boolean =
        this !is WalletException.DuplicatePaymentCallback &&
            this !is WalletException.PaymentCancelled &&
            this !is WalletException.PaymentNotConfirmed &&
            this !is WalletException.PaymentFailed
}
