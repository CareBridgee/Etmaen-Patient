package com.carenest.data.repository

import android.util.Log
import com.carenest.data.paymob.PaymobConfigProvider
import com.carenest.data.paymob.PaymobIntentionRequestFactory
import com.carenest.data.source.remote.ApiException
import com.carenest.data.source.remote.dto.paymob.PaymobRetrievedIntentionDto
import com.carenest.data.source.remote.service.PaymobApiService
import com.carenest.domain.model.home.User
import com.carenest.domain.model.payment.WalletException
import com.carenest.domain.model.payment.WalletTopUpAmount
import com.carenest.domain.model.payment.WalletTopUpAttemptState
import com.carenest.domain.model.payment.WalletTopUpPaymentResult
import com.carenest.domain.repository.PaymobNativeCheckoutLauncher
import com.carenest.domain.repository.PaymobNativeCheckoutRequest
import com.carenest.domain.repository.PaymobNativeCheckoutResult
import com.carenest.domain.repository.UserRepository
import com.carenest.domain.repository.WalletTopUpAttemptRepository
import com.carenest.domain.repository.WalletTopUpPaymentGateway
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull

@Singleton
class PaymobWalletTopUpPaymentGateway @Inject constructor(
    private val configuration: PaymobConfigProvider,
    private val paymobApiService: PaymobApiService,
    private val requestFactory: PaymobIntentionRequestFactory,
    private val userRepository: UserRepository,
    private val attemptRepository: WalletTopUpAttemptRepository,
    private val nativeCheckoutLauncher: PaymobNativeCheckoutLauncher,
) : WalletTopUpPaymentGateway {
    private val mutex = Mutex()

    override suspend fun startTopUp(amount: WalletTopUpAmount): Result<WalletTopUpPaymentResult> =
        mutex.withLock {
            attemptRepository.findCreditAddPendingAttempt()?.let {
                return@withLock Result.failure(WalletException.CreditAddPending)
            }
            attemptRepository.getActiveAttempt()?.let {
                return@withLock Result.failure(WalletException.PaymentAlreadyInProgress)
            }

            val config = configuration.current().getOrElse { return@withLock Result.failure(it) }
            if (!nativeCheckoutLauncher.isAvailable()) {
                return@withLock Result.failure(WalletException.PaymentSdkUnavailable)
            }

            val user = currentUser().getOrElse { return@withLock Result.failure(it) }
            val merchantReference = newMerchantReference()
            val attempt = attemptRepository.createAttempt(
                amount = amount,
                merchantReference = merchantReference,
            )

            val intentionRequest = requestFactory.create(
                amount = amount,
                integrationId = config.integrationId,
                merchantReference = merchantReference,
                postPayUrl = config.baseUrl.postPayUrl(),
                user = user,
            ).getOrElse { error ->
                attemptRepository.updateAttemptState(attempt.localAttemptId, WalletTopUpAttemptState.Failed)
                return@withLock Result.failure(error)
            }

            val intention = paymobApiService.createIntention(intentionRequest)
                .getOrElse { error ->
                    attemptRepository.updateAttemptState(attempt.localAttemptId, WalletTopUpAttemptState.Failed)
                    return@withLock Result.failure(error.toPaymobIntentionFailure())
                }

            val clientSecret = intention.clientSecret?.takeIf(String::isNotBlank)
                ?: run {
                    attemptRepository.updateAttemptState(attempt.localAttemptId, WalletTopUpAttemptState.Failed)
                    return@withLock Result.failure(WalletException.PaymentNotConfirmed)
                }

            attemptRepository.updateAttemptState(
                localAttemptId = attempt.localAttemptId,
                state = WalletTopUpAttemptState.IntentionCreated,
            )
            attemptRepository.updateAttemptState(
                localAttemptId = attempt.localAttemptId,
                state = WalletTopUpAttemptState.CheckoutStarted,
            )

            val checkoutResult = nativeCheckoutLauncher.launch(
                PaymobNativeCheckoutRequest(
                    publicKey = config.publicKey,
                    clientSecret = clientSecret,
                    appName = "CareNest",
                    buttonBackgroundColor = CARENEST_PRIMARY_COLOR,
                    buttonTextColor = WHITE,
                    saveCardDefault = false,
                    showSaveCard = true,
                ),
            ).getOrElse { error ->
                attemptRepository.updateAttemptState(attempt.localAttemptId, WalletTopUpAttemptState.Failed)
                return@withLock Result.failure(error)
            }

            when (checkoutResult) {
                is PaymobNativeCheckoutResult.Success -> {
                    val transactionId = checkoutResult.details.transactionReference()
                        ?: retrieveConfirmedPaymentReference(
                            publicKey = config.publicKey,
                            clientSecret = clientSecret,
                        )
                        ?: run {
                            attemptRepository.updateAttemptState(
                                localAttemptId = attempt.localAttemptId,
                                state = WalletTopUpAttemptState.Pending,
                            )
                            return@withLock Result.success(WalletTopUpPaymentResult.Pending)
                        }
                    attemptRepository.updateAttemptState(
                        localAttemptId = attempt.localAttemptId,
                        state = WalletTopUpAttemptState.PaymobSucceeded,
                        paymobTransactionId = transactionId,
                    )
                    Result.success(
                        WalletTopUpPaymentResult.Success(
                            transactionId = transactionId,
                            attemptId = attempt.localAttemptId,
                        ),
                    )
                }

                PaymobNativeCheckoutResult.Cancelled -> {
                    attemptRepository.updateAttemptState(attempt.localAttemptId, WalletTopUpAttemptState.Cancelled)
                    Result.success(WalletTopUpPaymentResult.Cancelled)
                }

                PaymobNativeCheckoutResult.Pending -> {
                    val transactionId = retrieveConfirmedPaymentReference(
                        publicKey = config.publicKey,
                        clientSecret = clientSecret,
                    )
                    if (transactionId != null) {
                        attemptRepository.updateAttemptState(
                            localAttemptId = attempt.localAttemptId,
                            state = WalletTopUpAttemptState.PaymobSucceeded,
                            paymobTransactionId = transactionId,
                        )
                        Result.success(
                            WalletTopUpPaymentResult.Success(
                                transactionId = transactionId,
                                attemptId = attempt.localAttemptId,
                            ),
                        )
                    } else {
                        attemptRepository.updateAttemptState(attempt.localAttemptId, WalletTopUpAttemptState.Pending)
                        Result.success(WalletTopUpPaymentResult.Pending)
                    }
                }

                is PaymobNativeCheckoutResult.Failed -> {
                    attemptRepository.updateAttemptState(attempt.localAttemptId, WalletTopUpAttemptState.Failed)
                    Result.success(WalletTopUpPaymentResult.Failed(checkoutResult.reason))
                }

                PaymobNativeCheckoutResult.Unknown -> {
                    val transactionId = retrieveConfirmedPaymentReference(
                        publicKey = config.publicKey,
                        clientSecret = clientSecret,
                    )
                    if (transactionId != null) {
                        attemptRepository.updateAttemptState(
                            localAttemptId = attempt.localAttemptId,
                            state = WalletTopUpAttemptState.PaymobSucceeded,
                            paymobTransactionId = transactionId,
                        )
                        Result.success(
                            WalletTopUpPaymentResult.Success(
                                transactionId = transactionId,
                                attemptId = attempt.localAttemptId,
                            ),
                        )
                    } else {
                        attemptRepository.updateAttemptState(attempt.localAttemptId, WalletTopUpAttemptState.Pending)
                        Result.success(WalletTopUpPaymentResult.Pending)
                    }
                }
            }
        }

    private suspend fun currentUser(): Result<User> {
        val cached = userRepository.observeCurrentUser().first()
        if (cached != null && cached.id.isNotBlank()) return Result.success(cached)
        return userRepository.refreshCurrentUser()
            .mapCatching { user ->
                if (user.id.isBlank()) throw WalletException.MissingAuthenticatedUserId
                user
            }
    }

    private fun newMerchantReference(): String =
        "carenest-wallet-${UUID.randomUUID()}"

    private fun String.postPayUrl(): String =
        "${trim().trimEnd('/')}/api/acceptance/post_pay"

    private fun Map<String, String>.transactionReference(): String? {
        val candidateKeys = listOf(
            "transactionId",
            "transaction_id",
            "txn_id",
            "transaction",
            "id",
            "payment_id",
            "paymentId",
            "order",
            "order_id",
            "merchant_order_id",
            "reference",
            "special_reference",
        )
        return candidateKeys
            .firstNotNullOfOrNull { key -> this[key]?.takeIf(::isSafeReference) }
    }

    private suspend fun retrieveConfirmedPaymentReference(
        publicKey: String,
        clientSecret: String,
    ): String? {
        repeat(CONFIRMATION_RETRY_COUNT) { attemptIndex ->
            val retrievedResult = paymobApiService.retrieveIntention(
                publicKey = publicKey,
                clientSecret = clientSecret,
            )
            retrievedResult.onFailure { error ->
                log(
                    "Retrieve intention failed. attempt=${attemptIndex + 1} reason=${
                        error.message.orEmpty().take(MAX_FAILURE_REASON_LENGTH)
                    }",
                )
            }

            val retrievedIntention = retrievedResult.getOrNull()
            if (retrievedIntention != null) {
                log(
                    "Retrieve intention result. attempt=${attemptIndex + 1} " +
                        "confirmed=${retrievedIntention.confirmed} " +
                        "status=${retrievedIntention.status.orEmpty()} " +
                        "transactionRecords=${retrievedIntention.transactionRecords.size} " +
                        "transactions=${retrievedIntention.transactions.size}",
                )
            }

            val reference = retrievedIntention?.confirmedTransactionReference()
            if (reference != null) {
                log("Retrieve intention confirmed payment reference.")
                return reference
            }

            if (attemptIndex < CONFIRMATION_RETRY_COUNT - 1) {
                delay(CONFIRMATION_RETRY_DELAY_MS)
            }
        }
        return null
    }

    private fun PaymobRetrievedIntentionDto.confirmedTransactionReference(): String? {
        if (!isConfirmedPayment()) return null
        return (transactionRecords + transactions)
            .firstNotNullOfOrNull { it.transactionReference() }
    }

    private fun PaymobRetrievedIntentionDto.isConfirmedPayment(): Boolean =
        confirmed == true ||
            status.equals("confirmed", ignoreCase = true) ||
            status.equals("paid", ignoreCase = true) ||
            status.equals("success", ignoreCase = true)

    private fun JsonElement.transactionReference(): String? =
        (this as? JsonObject)?.let { transaction ->
            RetrievedTransactionKeys.firstNotNullOfOrNull { key ->
                transaction[key]?.safeStringReference()
            }
        }

    private fun JsonElement.safeStringReference(): String? =
        (this as? JsonPrimitive)
            ?.contentOrNull
            ?.takeIf(::isSafeReference)

    private fun isSafeReference(value: String): Boolean =
        value.isNotBlank() && value.length <= 128 && value.all { it.isLetterOrDigit() || it in "-_." }

    private fun log(message: String) {
        runCatching { Log.d(TAG, message) }
    }

    private fun Throwable.toPaymobIntentionFailure(): WalletException =
        when {
            this is ApiException && statusCode == 404 -> WalletException.PaymentIntegrationInvalid
            this is ApiException && statusCode in setOf(401, 403) -> WalletException.PaymentConfigurationMissing
            this is ApiException && message?.isNotBlank() == true ->
                WalletException.PaymentFailed(message.orEmpty().take(MAX_FAILURE_REASON_LENGTH))
            else -> WalletException.PaymentFailed("Could not create Paymob payment intention")
        }

    private companion object {
        val CARENEST_PRIMARY_COLOR = 0xFF0D6EFD.toInt()
        val WHITE = 0xFFFFFFFF.toInt()
        const val TAG = "CareNestPaymob"
        const val MAX_FAILURE_REASON_LENGTH = 180
        const val CONFIRMATION_RETRY_COUNT = 3
        const val CONFIRMATION_RETRY_DELAY_MS = 1_000L
        val RetrievedTransactionKeys = listOf(
            "transactionId",
            "transaction_id",
            "txn_id",
            "transaction",
            "id",
            "payment_id",
            "paymentId",
            "order",
            "order_id",
            "merchant_order_id",
            "reference",
            "special_reference",
        )
    }
}
