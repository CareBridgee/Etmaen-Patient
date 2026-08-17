package com.carenest.domain.model.payment

import java.math.BigDecimal
import java.math.RoundingMode

data class WalletCredit(
    val credit: Double,
)

data class WalletTopUpAmount private constructor(
    val egp: String,
    val minorUnits: Long,
    val decimal: BigDecimal,
) {
    companion object {
        private val DecimalPattern = Regex("""^\d+(\.\d{1,2})?$""")

        fun parse(rawAmount: String): Result<WalletTopUpAmount> = runCatching {
            val normalized = rawAmount.trim()
            if (!DecimalPattern.matches(normalized)) {
                throw WalletException.InvalidAmount
            }

            val decimal = normalized.toBigDecimal().setScale(2, RoundingMode.UNNECESSARY)
            if (decimal <= BigDecimal.ZERO) {
                throw WalletException.InvalidAmount
            }

            val minorUnits = decimal.movePointRight(2).longValueExact()
            WalletTopUpAmount(
                egp = decimal.toPlainString(),
                minorUnits = minorUnits,
                decimal = decimal,
            )
        }.recoverCatching { error ->
            if (error is WalletException) throw error
            throw WalletException.InvalidAmount
        }
    }
}

enum class ServicePaymentMethod(
    val paymentType: PaymentType,
) {
    Cash(PaymentType.Cash),
    Wallet(PaymentType.Credit),
}

enum class PaymentType(
    val backendValue: String,
) {
    Cash("CASH"),
    Credit("CREDIT"),
}

enum class WalletOperation(
    val backendValue: String,
) {
    Add("ADD"),
    Deduct("DEDUCT"),
}

sealed class WalletException(message: String) : Exception(message) {
    data object InvalidAmount : WalletException("Wallet amount must be positive")
    data object MissingAuthenticatedUserId : WalletException("Authenticated user id is missing")
    data object InsufficientCredit : WalletException("Insufficient wallet credit")
    data object PaymentUnavailable : WalletException("Paymob payment is not configured")
    data object PaymentSdkUnavailable : WalletException("Paymob Native Checkout SDK is not available")
    data object PaymentConfigurationMissing : WalletException("Paymob configuration is incomplete")
    data object PaymentIntegrationInvalid : WalletException("Paymob card integration is invalid for these credentials")
    data object PaymentCancelled : WalletException("Payment was cancelled")
    data object PaymentNotConfirmed : WalletException("Payment was not confirmed")
    data object DuplicatePaymentCallback : WalletException("Payment success callback was already processed")
    data object CreditAddPending : WalletException("Payment succeeded but wallet credit still needs to be added")
    data object PaymentAlreadyInProgress : WalletException("A wallet top-up is already in progress")
    data class PaymentFailed(override val message: String) : WalletException(message)
}

sealed interface WalletTopUpPaymentResult {
    data class Success(
        val transactionId: String,
        val attemptId: String? = null,
    ) : WalletTopUpPaymentResult

    data object Cancelled : WalletTopUpPaymentResult
    data object Pending : WalletTopUpPaymentResult
    data class Failed(val reason: String) : WalletTopUpPaymentResult
}

enum class WalletTopUpAttemptState {
    Created,
    IntentionCreated,
    CheckoutStarted,
    PaymobSucceeded,
    Cancelled,
    Failed,
    Pending,
    CreditAddPending,
    Credited,
}

data class WalletTopUpAttempt(
    val localAttemptId: String,
    val merchantReference: String,
    val amount: WalletTopUpAmount,
    val state: WalletTopUpAttemptState,
    val paymobTransactionId: String? = null,
    val creditAddSucceeded: Boolean = false,
    val createdAtEpochMillis: Long = 0L,
    val updatedAtEpochMillis: Long = 0L,
)
