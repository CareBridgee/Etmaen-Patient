package com.carenest.data.repository

import com.carenest.domain.model.payment.WalletException
import com.carenest.domain.model.payment.WalletTopUpAmount
import com.carenest.domain.model.payment.WalletTopUpPaymentResult
import com.carenest.domain.repository.WalletTopUpPaymentGateway
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UnavailableWalletTopUpPaymentGateway @Inject constructor() : WalletTopUpPaymentGateway {
    override suspend fun startTopUp(amount: WalletTopUpAmount): Result<WalletTopUpPaymentResult> =
        Result.failure(WalletException.PaymentUnavailable)
}
