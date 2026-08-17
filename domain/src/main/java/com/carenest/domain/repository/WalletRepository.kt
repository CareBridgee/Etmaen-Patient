package com.carenest.domain.repository

import com.carenest.domain.model.payment.WalletCredit
import com.carenest.domain.model.payment.WalletOperation
import com.carenest.domain.model.payment.WalletTopUpAmount

interface WalletRepository {
    suspend fun getCredit(): Result<WalletCredit>
    suspend fun updateCredit(amount: WalletTopUpAmount, operation: WalletOperation): Result<WalletCredit>
}
