package com.carenest.data.repository

import com.carenest.data.paymob.PaymobConfig
import com.carenest.data.paymob.PaymobConfigProvider
import com.carenest.data.paymob.PaymobIntentionRequestFactory
import com.carenest.data.source.remote.dto.paymob.PaymobIntentionRequestDto
import com.carenest.data.source.remote.dto.paymob.PaymobIntentionResponseDto
import com.carenest.data.source.remote.dto.paymob.PaymobRetrievedIntentionDto
import com.carenest.data.source.remote.service.PaymobApiService
import com.carenest.domain.model.PaymobNativeCheckoutRequest
import com.carenest.domain.model.home.User
import com.carenest.domain.model.payment.WalletException
import com.carenest.domain.model.payment.WalletTopUpAmount
import com.carenest.domain.model.payment.WalletTopUpAttempt
import com.carenest.domain.model.payment.WalletTopUpAttemptState
import com.carenest.domain.model.payment.WalletTopUpPaymentResult
import com.carenest.domain.model.user.UserUpdate
import com.carenest.domain.repository.PaymobNativeCheckoutLauncher
import com.carenest.domain.repository.PaymobNativeCheckoutResult
import com.carenest.domain.repository.UserRepository
import com.carenest.domain.repository.WalletTopUpAttemptRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

class PaymobWalletTopUpPaymentGatewayTest {
    @Test
    fun `missing native SDK stops before creating Paymob Intention`() = runTest {
        val api = RecordingPaymobApiService()
        val gateway = gateway(
            api = api,
            launcher = FakeNativeCheckoutLauncher(available = false),
        )

        val result = gateway.startTopUp(amount())

        assertTrue(result.exceptionOrNull() is WalletException.PaymentSdkUnavailable)
        assertTrue(api.requests.isEmpty())
    }

    @Test
    fun `missing configuration stops before creating Paymob Intention`() = runTest {
        val api = RecordingPaymobApiService()
        val gateway = gateway(
            configProvider = FakePaymobConfigProvider(Result.failure(WalletException.PaymentConfigurationMissing)),
            api = api,
            launcher = FakeNativeCheckoutLauncher(available = true),
        )

        val result = gateway.startTopUp(amount())

        assertTrue(result.exceptionOrNull() is WalletException.PaymentConfigurationMissing)
        assertTrue(api.requests.isEmpty())
    }

    @Test
    fun `active non-terminal attempt stops before creating another Paymob Intention`() = runTest {
        val api = RecordingPaymobApiService()
        val attempts = InMemoryTopUpAttemptRepository().apply {
            attempt = WalletTopUpAttempt(
                localAttemptId = "attempt-1",
                merchantReference = "ref-1",
                amount = amount(),
                state = WalletTopUpAttemptState.CheckoutStarted,
            )
        }
        val gateway = gateway(api = api, attempts = attempts)

        val result = gateway.startTopUp(amount())

        assertTrue(result.exceptionOrNull() is WalletException.PaymentAlreadyInProgress)
        assertTrue(api.requests.isEmpty())
    }

    @Test
    fun `successful checkout with transaction id returns success`() = runTest {
        val api = RecordingPaymobApiService()
        val attempts = InMemoryTopUpAttemptRepository()
        val gateway = gateway(
            api = api,
            attempts = attempts,
            launcher = FakeNativeCheckoutLauncher(
                result = PaymobNativeCheckoutResult.Success(mapOf("transactionId" to "txn-123")),
            ),
        )

        val result = gateway.startTopUp(amount()).getOrThrow()

        assertTrue(result is WalletTopUpPaymentResult.Success)
        assertEquals(50_000L, api.requests.single().amount)
        assertEquals(listOf(5855102), api.requests.single().paymentMethods)
        assertEquals(WalletTopUpAttemptState.PaymobSucceeded, attempts.attempt?.state)
        assertEquals("txn-123", attempts.attempt?.paymobTransactionId)
    }

    @Test
    fun `successful checkout without transaction id is not success`() = runTest {
        val gateway = gateway(
            launcher = FakeNativeCheckoutLauncher(
                result = PaymobNativeCheckoutResult.Success(emptyMap()),
            ),
        )

        val result = gateway.startTopUp(amount()).getOrThrow()

        assertEquals(WalletTopUpPaymentResult.Pending, result)
    }

    @Test
    fun `pending checkout with confirmed retrieved transaction returns success`() = runTest {
        val attempts = InMemoryTopUpAttemptRepository()
        val gateway = gateway(
            api = RecordingPaymobApiService(
                retrievedIntention = PaymobRetrievedIntentionDto(
                    confirmed = true,
                    status = "confirmed",
                    transactions = listOf(
                        JsonObject(mapOf("id" to JsonPrimitive(123456))),
                    ),
                ),
            ),
            attempts = attempts,
            launcher = FakeNativeCheckoutLauncher(
                result = PaymobNativeCheckoutResult.Pending,
            ),
        )

        val result = gateway.startTopUp(amount()).getOrThrow()

        assertEquals(WalletTopUpPaymentResult.Success("123456", "attempt-1"), result)
        assertEquals(WalletTopUpAttemptState.PaymobSucceeded, attempts.attempt?.state)
        assertEquals("123456", attempts.attempt?.paymobTransactionId)
    }

    @Test
    fun `cancelled failed and pending checkout are not success`() = runTest {
        val outcomes = listOf(
            PaymobNativeCheckoutResult.Cancelled to WalletTopUpPaymentResult.Cancelled,
            PaymobNativeCheckoutResult.Pending to WalletTopUpPaymentResult.Pending,
            PaymobNativeCheckoutResult.Failed("declined") to WalletTopUpPaymentResult.Failed("declined"),
        )

        outcomes.forEach { (nativeResult, expected) ->
            val gateway = gateway(launcher = FakeNativeCheckoutLauncher(result = nativeResult))

            val result = gateway.startTopUp(amount()).getOrThrow()

            assertEquals(expected, result)
        }
    }

    private fun gateway(
        configProvider: PaymobConfigProvider = FakePaymobConfigProvider(),
        api: RecordingPaymobApiService = RecordingPaymobApiService(),
        attempts: InMemoryTopUpAttemptRepository = InMemoryTopUpAttemptRepository(),
        launcher: FakeNativeCheckoutLauncher = FakeNativeCheckoutLauncher(),
    ): PaymobWalletTopUpPaymentGateway =
        PaymobWalletTopUpPaymentGateway(
            configuration = configProvider,
            paymobApiService = api,
            requestFactory = PaymobIntentionRequestFactory(),
            userRepository = FakeUserRepository(),
            attemptRepository = attempts,
            nativeCheckoutLauncher = launcher,
        )

    private fun amount(): WalletTopUpAmount =
        WalletTopUpAmount.parse("500.00").getOrThrow()
}

private class FakePaymobConfigProvider(
    private val result: Result<PaymobConfig> = Result.success(
        PaymobConfig(
            secretKey = "test-secret",
            publicKey = "test-public",
            integrationId = 5855102,
            baseUrl = "https://accept.paymob.com",
        ),
    ),
) : PaymobConfigProvider {
    override fun current(): Result<PaymobConfig> = result
}

private class RecordingPaymobApiService(
    private val retrievedIntention: PaymobRetrievedIntentionDto = PaymobRetrievedIntentionDto(),
) : PaymobApiService {
    val requests = mutableListOf<PaymobIntentionRequestDto>()
    val retrieveRequests = mutableListOf<Pair<String, String>>()

    override suspend fun createIntention(
        request: PaymobIntentionRequestDto,
    ): Result<PaymobIntentionResponseDto> {
        requests += request
        return Result.success(PaymobIntentionResponseDto(clientSecret = "client-secret"))
    }

    override suspend fun retrieveIntention(
        publicKey: String,
        clientSecret: String,
    ): Result<PaymobRetrievedIntentionDto> {
        retrieveRequests += publicKey to clientSecret
        return Result.success(retrievedIntention)
    }
}

private class FakeNativeCheckoutLauncher(
    private val available: Boolean = true,
    private val result: PaymobNativeCheckoutResult =
        PaymobNativeCheckoutResult.Success(mapOf("transactionId" to "txn-123")),
) : PaymobNativeCheckoutLauncher {
    override fun isAvailable(): Boolean = available

    override suspend fun launch(
        request: PaymobNativeCheckoutRequest,
    ): Result<PaymobNativeCheckoutResult> =
        Result.success(result)
}

private class InMemoryTopUpAttemptRepository : WalletTopUpAttemptRepository {
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
        attempt?.takeIf {
            it.state == WalletTopUpAttemptState.CreditAddPending && !it.creditAddSucceeded
        }

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

private class FakeUserRepository : UserRepository {
    private val user = MutableStateFlow(
        User(
            id = "user-id",
            phoneNumber = "+201000000000",
            email = "patient@example.com",
            firstName = "Care",
            lastName = "Nest",
        ),
    )

    override fun observeCurrentUser(): Flow<User?> = user

    override suspend fun refreshCurrentUser(): Result<User> =
        Result.success(user.value)

    override suspend fun uploadProfileImage(
        fileName: String,
        contentType: String,
        bytes: ByteArray,
    ): Result<String> = Result.failure(UnsupportedOperationException())

    override suspend fun updateCurrentUser(update: UserUpdate): Result<User> =
        Result.failure(UnsupportedOperationException())

    override suspend fun clearCurrentUser() {
        user.value = User()
    }
}
