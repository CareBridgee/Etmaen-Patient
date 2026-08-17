package com.carenest.domain.usecase.wallet

import com.carenest.domain.model.payment.WalletCredit
import com.carenest.domain.model.payment.WalletException
import com.carenest.domain.model.payment.WalletOperation
import com.carenest.domain.model.payment.WalletTopUpAmount
import com.carenest.domain.model.payment.WalletTopUpAttemptState
import com.carenest.domain.model.payment.WalletTopUpPaymentResult
import com.carenest.domain.repository.WalletOperationGuardRepository
import com.carenest.domain.repository.WalletRepository
import com.carenest.domain.repository.WalletTopUpAttemptRepository
import javax.inject.Inject
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class AddWalletCreditAfterPaymentUseCase @Inject constructor(
    private val walletRepository: WalletRepository,
    private val guardRepository: WalletOperationGuardRepository,
    private val attemptRepository: WalletTopUpAttemptRepository,
) {
    private val mutex = Mutex()

    suspend operator fun invoke(
        amount: WalletTopUpAmount,
        paymentResult: WalletTopUpPaymentResult,
    ): Result<WalletCredit> {
        return when (paymentResult) {
            WalletTopUpPaymentResult.Cancelled ->
                Result.failure(WalletException.PaymentCancelled)

            is WalletTopUpPaymentResult.Failed ->
                Result.failure(WalletException.PaymentFailed(paymentResult.reason))

            WalletTopUpPaymentResult.Pending ->
                Result.failure(WalletException.PaymentNotConfirmed)

            is WalletTopUpPaymentResult.Success -> mutex.withLock {
                val transactionId = paymentResult.transactionId.takeIf(::isSafeTransactionId)
                    ?: return@withLock Result.failure(WalletException.PaymentNotConfirmed)

                if (guardRepository.isTopUpProcessed(paymentResult.transactionId)) {
                    return@withLock Result.failure(WalletException.DuplicatePaymentCallback)
                }

                paymentResult.attemptId?.let { attemptId ->
                    attemptRepository.updateAttemptState(
                        localAttemptId = attemptId,
                        state = WalletTopUpAttemptState.PaymobSucceeded,
                        paymobTransactionId = transactionId,
                    )
                }

                walletRepository.updateCredit(amount, WalletOperation.Add)
                    .onSuccess {
                        guardRepository.markTopUpProcessed(transactionId)
                        paymentResult.attemptId?.let { attemptId ->
                            attemptRepository.updateAttemptState(
                                localAttemptId = attemptId,
                                state = WalletTopUpAttemptState.Credited,
                                paymobTransactionId = transactionId,
                                creditAddSucceeded = true,
                            )
                        }
                    }
                    .onFailure {
                        paymentResult.attemptId?.let { attemptId ->
                            attemptRepository.updateAttemptState(
                                localAttemptId = attemptId,
                                state = WalletTopUpAttemptState.CreditAddPending,
                                paymobTransactionId = transactionId,
                                creditAddSucceeded = false,
                            )
                        }
                    }
            }
        }
    }

    private fun isSafeTransactionId(value: String): Boolean =
        value.isNotBlank() && value.length <= 128 && value.all { it.isLetterOrDigit() || it in "-_." }
}
