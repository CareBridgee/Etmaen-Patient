package com.carenest.domain.usecase.wallet

import com.carenest.domain.model.payment.WalletCredit
import com.carenest.domain.model.payment.WalletOperation
import com.carenest.domain.model.payment.WalletTopUpAmount
import com.carenest.domain.repository.WalletRepository
import javax.inject.Inject

class UpdateWalletCreditUseCase @Inject constructor(
    private val repository: WalletRepository,
) {
    suspend operator fun invoke(
        amount: WalletTopUpAmount,
        operation: WalletOperation,
    ): Result<WalletCredit> {
        return repository.updateCredit(amount, operation)
    }
}
