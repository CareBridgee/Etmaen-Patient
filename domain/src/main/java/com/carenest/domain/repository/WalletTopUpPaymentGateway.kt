package com.carenest.domain.repository

import com.carenest.domain.model.payment.WalletTopUpAmount
import com.carenest.domain.model.payment.WalletTopUpPaymentResult

interface WalletTopUpPaymentGateway {
    suspend fun startTopUp(amount: WalletTopUpAmount): Result<WalletTopUpPaymentResult>
}
