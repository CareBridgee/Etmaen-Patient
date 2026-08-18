package com.carenest.domain.usecase.wallet

import com.carenest.domain.model.payment.WalletCredit
import com.carenest.domain.model.payment.WalletException
import com.carenest.domain.model.payment.WalletOperation
import com.carenest.domain.model.payment.WalletTopUpAttemptState
import com.carenest.domain.repository.WalletOperationGuardRepository
import com.carenest.domain.repository.WalletRepository
import com.carenest.domain.repository.WalletTopUpAttemptRepository
import javax.inject.Inject
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class RetryPendingWalletTopUpCreditUseCase @Inject constructor(
    private val walletRepository: WalletRepository,
    private val guardRepository: WalletOperationGuardRepository,
    private val attemptRepository: WalletTopUpAttemptRepository,
) {
    private val mutex = Mutex()

    suspend operator fun invoke(): Result<WalletCredit> = mutex.withLock {
        val attempt = attemptRepository.findCreditAddPendingAttempt()
            ?: return@withLock Result.failure(WalletException.PaymentNotConfirmed)
        val transactionId = attempt.paymobTransactionId
            ?.takeIf { it.isNotBlank() }
            ?: return@withLock Result.failure(WalletException.PaymentNotConfirmed)

        if (guardRepository.isTopUpProcessed(transactionId)) {
            attemptRepository.updateAttemptState(
                localAttemptId = attempt.localAttemptId,
                state = WalletTopUpAttemptState.Credited,
                paymobTransactionId = transactionId,
                creditAddSucceeded = true,
            )
            return@withLock walletRepository.getCredit()
        }

        walletRepository.updateCredit(attempt.amount, WalletOperation.Add)
            .onSuccess {
                guardRepository.markTopUpProcessed(transactionId)
                attemptRepository.updateAttemptState(
                    localAttemptId = attempt.localAttemptId,
                    state = WalletTopUpAttemptState.Credited,
                    paymobTransactionId = transactionId,
                    creditAddSucceeded = true,
                )
            }
            .onFailure {
                attemptRepository.updateAttemptState(
                    localAttemptId = attempt.localAttemptId,
                    state = WalletTopUpAttemptState.CreditAddPending,
                    paymobTransactionId = transactionId,
                    creditAddSucceeded = false,
                )
            }
    }
}
