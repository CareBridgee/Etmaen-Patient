package com.carenest.domain.usecase.wallet

import com.carenest.domain.model.payment.PaymentType
import com.carenest.domain.model.payment.ServicePaymentMethod
import com.carenest.domain.model.payment.WalletCredit
import com.carenest.domain.model.payment.WalletException
import com.carenest.domain.model.payment.WalletOperation
import com.carenest.domain.model.payment.WalletTopUpAmount
import com.carenest.domain.model.payment.WalletTopUpAttempt
import com.carenest.domain.model.payment.WalletTopUpAttemptState
import com.carenest.domain.model.payment.WalletTopUpPaymentResult
import com.carenest.domain.repository.WalletOperationGuardRepository
import com.carenest.domain.repository.WalletRepository
import com.carenest.domain.repository.WalletTopUpAttemptRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WalletPaymentUseCasesTest {
    @Test
    fun `cash maps to CASH`() {
        assertEquals(PaymentType.Cash, ServicePaymentMethod.Cash.paymentType)
        assertEquals("CASH", ServicePaymentMethod.Cash.paymentType.backendValue)
    }

    @Test
    fun `wallet maps to CREDIT`() {
        assertEquals(PaymentType.Credit, ServicePaymentMethod.Wallet.paymentType)
        assertEquals("CREDIT", ServicePaymentMethod.Wallet.paymentType.backendValue)
    }

    @Test
    fun `add credit is called only after explicit payment success`() = runTest {
        val wallet = RecordingWalletRepository()
        val useCase = AddWalletCreditAfterPaymentUseCase(wallet, InMemoryGuardRepository(), InMemoryAttemptRepository())

        val result = useCase(
            amount = amount("500.00"),
            paymentResult = WalletTopUpPaymentResult.Success("txn-1"),
        ).getOrThrow()

        assertEquals("500.00", wallet.updatedAmounts.single())
        assertEquals(WalletOperation.Add, wallet.updatedOperations.single())
        assertEquals(500.0, result.credit, 0.0)
    }

    @Test
    fun `payment cancellation and failure never add credit`() = runTest {
        val wallet = RecordingWalletRepository()
        val useCase = AddWalletCreditAfterPaymentUseCase(wallet, InMemoryGuardRepository(), InMemoryAttemptRepository())

        val cancelled = useCase(amount("500.00"), WalletTopUpPaymentResult.Cancelled)
        val failed = useCase(amount("500.00"), WalletTopUpPaymentResult.Failed("declined"))

        assertTrue(cancelled.exceptionOrNull() is WalletException.PaymentCancelled)
        assertTrue(failed.exceptionOrNull() is WalletException.PaymentFailed)
        assertTrue(wallet.updatedAmounts.isEmpty())
    }

    @Test
    fun `duplicate payment success callback does not add credit twice`() = runTest {
        val wallet = RecordingWalletRepository()
        val useCase = AddWalletCreditAfterPaymentUseCase(wallet, InMemoryGuardRepository(), InMemoryAttemptRepository())

        useCase(amount("500.00"), WalletTopUpPaymentResult.Success("txn-1"))
        val duplicate = useCase(amount("500.00"), WalletTopUpPaymentResult.Success("txn-1"))

        assertEquals(1, wallet.updatedAmounts.size)
        assertTrue(duplicate.exceptionOrNull() is WalletException.DuplicatePaymentCallback)
    }

    @Test
    fun `failed credit update remains retryable`() = runTest {
        val wallet = RecordingWalletRepository().apply {
            failure = WalletException.InsufficientCredit
        }
        val guard = InMemoryGuardRepository()
        val attempts = InMemoryAttemptRepository().apply {
            attempt = WalletTopUpAttempt(
                localAttemptId = "attempt-1",
                merchantReference = "ref-1",
                amount = amount("500.00"),
                state = WalletTopUpAttemptState.PaymobSucceeded,
                paymobTransactionId = "txn-1",
            )
        }
        val useCase = AddWalletCreditAfterPaymentUseCase(wallet, guard, attempts)

        val failed = useCase(amount("500.00"), WalletTopUpPaymentResult.Success("txn-1", "attempt-1"))
        assertTrue(failed.exceptionOrNull() is WalletException.InsufficientCredit)
        assertEquals(WalletTopUpAttemptState.CreditAddPending, attempts.attempt?.state)

        wallet.failure = null
        val retry = RetryPendingWalletTopUpCreditUseCase(wallet, guard, attempts)()

        assertEquals(2, wallet.updatedAmounts.size)
        assertEquals(500.0, retry.getOrThrow().credit, 0.0)
        assertEquals(WalletTopUpAttemptState.Credited, attempts.attempt?.state)
    }

    @Test
    fun `retry pending credit add does not start another payment`() = runTest {
        val wallet = RecordingWalletRepository()
        val attempts = InMemoryAttemptRepository().apply {
            attempt = WalletTopUpAttempt(
                localAttemptId = "attempt-1",
                merchantReference = "ref-1",
                amount = amount("250.00"),
                state = WalletTopUpAttemptState.CreditAddPending,
                paymobTransactionId = "txn-2",
                creditAddSucceeded = false,
            )
        }

        val result = RetryPendingWalletTopUpCreditUseCase(
            walletRepository = wallet,
            guardRepository = InMemoryGuardRepository(),
            attemptRepository = attempts,
        )().getOrThrow()

        assertEquals(listOf("250.00"), wallet.updatedAmounts)
        assertEquals(WalletOperation.Add, wallet.updatedOperations.single())
        assertEquals(250.0, result.credit, 0.0)
        assertEquals(WalletTopUpAttemptState.Credited, attempts.attempt?.state)
    }

    private fun amount(value: String): WalletTopUpAmount =
        WalletTopUpAmount.parse(value).getOrThrow()
}

private class RecordingWalletRepository : WalletRepository {
    val updatedAmounts = mutableListOf<String>()
    val updatedOperations = mutableListOf<WalletOperation>()
    var failure: Throwable? = null

    override suspend fun getCredit(): Result<WalletCredit> = Result.success(WalletCredit(0.0))

    override suspend fun updateCredit(
        amount: WalletTopUpAmount,
        operation: WalletOperation,
    ): Result<WalletCredit> {
        updatedAmounts += amount.egp
        updatedOperations += operation
        failure?.let { return Result.failure(it) }
        return Result.success(WalletCredit(amount.egp.toDouble()))
    }
}

private class InMemoryGuardRepository : WalletOperationGuardRepository {
    private val topUps = mutableSetOf<String>()
    private val deductions = mutableSetOf<String>()

    override suspend fun isTopUpProcessed(transactionId: String): Boolean =
        transactionId in topUps

    override suspend fun markTopUpProcessed(transactionId: String) {
        topUps += transactionId
    }

    override suspend fun isDeductionProcessed(serviceRequestId: String): Boolean =
        serviceRequestId in deductions

    override suspend fun markDeductionProcessed(serviceRequestId: String) {
        deductions += serviceRequestId
    }
}

private class InMemoryAttemptRepository : WalletTopUpAttemptRepository {
    var attempt: WalletTopUpAttempt? = null

    override suspend fun getActiveAttempt(): WalletTopUpAttempt? = attempt

    override suspend fun saveAttempt(attempt: WalletTopUpAttempt) {
        this.attempt = attempt
    }

    override suspend fun updateAttemptState(
        localAttemptId: String,
        state: WalletTopUpAttemptState,
        paymobTransactionId: String?,
        creditAddSucceeded: Boolean?,
    ) {
        attempt = attempt?.copy(
            state = state,
            paymobTransactionId = paymobTransactionId ?: attempt?.paymobTransactionId,
            creditAddSucceeded = creditAddSucceeded ?: attempt?.creditAddSucceeded ?: false,
        )
    }

    override suspend fun findCreditAddPendingAttempt(): WalletTopUpAttempt? =
        attempt?.takeIf { it.state == WalletTopUpAttemptState.CreditAddPending }

    override suspend fun createAttempt(
        amount: WalletTopUpAmount,
        merchantReference: String,
    ): WalletTopUpAttempt =
        WalletTopUpAttempt(
            localAttemptId = "attempt-1",
            merchantReference = merchantReference,
            amount = amount,
            state = WalletTopUpAttemptState.Created,
        ).also { attempt = it }
}
