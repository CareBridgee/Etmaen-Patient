package com.carenest.domain.usecase.wallet

import com.carenest.domain.model.payment.WalletTopUpAttempt
import com.carenest.domain.repository.WalletTopUpAttemptRepository
import javax.inject.Inject

class GetPendingWalletTopUpAttemptUseCase @Inject constructor(
    private val attemptRepository: WalletTopUpAttemptRepository,
) {
    suspend operator fun invoke(): WalletTopUpAttempt? =
        attemptRepository.findCreditAddPendingAttempt()
}
