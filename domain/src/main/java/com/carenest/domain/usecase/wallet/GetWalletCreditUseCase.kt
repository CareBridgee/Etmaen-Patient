package com.carenest.domain.usecase.wallet

import com.carenest.domain.model.payment.WalletCredit
import com.carenest.domain.repository.WalletRepository
import javax.inject.Inject

class GetWalletCreditUseCase @Inject constructor(
    private val repository: WalletRepository,
) {
    suspend operator fun invoke(): Result<WalletCredit> = repository.getCredit()
}
