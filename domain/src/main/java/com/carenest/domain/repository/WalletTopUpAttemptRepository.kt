package com.carenest.domain.repository

import com.carenest.domain.model.payment.WalletTopUpAmount
import com.carenest.domain.model.payment.WalletTopUpAttempt
import com.carenest.domain.model.payment.WalletTopUpAttemptState

interface WalletTopUpAttemptRepository {
    suspend fun getActiveAttempt(): WalletTopUpAttempt?
    suspend fun saveAttempt(attempt: WalletTopUpAttempt)
    suspend fun updateAttemptState(
        localAttemptId: String,
        state: WalletTopUpAttemptState,
        paymobTransactionId: String? = null,
        creditAddSucceeded: Boolean? = null,
    )

    suspend fun findCreditAddPendingAttempt(): WalletTopUpAttempt?

    suspend fun createAttempt(
        amount: WalletTopUpAmount,
        merchantReference: String,
    ): WalletTopUpAttempt
}
