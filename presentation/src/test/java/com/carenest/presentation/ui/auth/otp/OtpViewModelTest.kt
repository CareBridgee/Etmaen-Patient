package com.carenest.presentation.ui.auth.otp

import com.carenest.domain.model.auth.AuthResult
import com.carenest.domain.repository.AuthRepository
import com.carenest.domain.repository.ProfileRepository
import com.carenest.domain.usecase.auth.RequestDevOtpUseCase
import com.carenest.domain.usecase.auth.VerifyOtpUseCase
import com.carenest.domain.usecase.user.GetAuthenticatedDestinationUseCase
import java.lang.reflect.Proxy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OtpViewModelTest {
    private lateinit var dispatcher: TestDispatcher
    private lateinit var authRepository: FakeOtpAuthRepository

    @Before
    fun setUp() {
        dispatcher = StandardTestDispatcher()
        Dispatchers.setMain(dispatcher)
        authRepository = FakeOtpAuthRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun countdownStartsAtThirtyAndDecreasesWithoutRealWaiting() = runTest(dispatcher) {
        val viewModel = viewModel()

        assertEquals(30, viewModel.state.value.remainingSeconds)
        advanceTimeBy(1_000)
        runCurrent()
        assertEquals(29, viewModel.state.value.remainingSeconds)
        advanceUntilIdle()
        assertEquals(0, viewModel.state.value.remainingSeconds)
        assertTrue(viewModel.state.value.canResend)
    }

    @Test
    fun resendIsUnavailableBeforeCountdownCompletes() = runTest(dispatcher) {
        val viewModel = viewModel()
        viewModel.onEvent(OtpIntent.PhoneNumberChanged("+201027642749"))

        viewModel.onEvent(OtpIntent.ResendClicked)
        runCurrent()

        assertEquals(0, authRepository.requestCount)
        assertFalse(viewModel.state.value.canResend)
        advanceUntilIdle()
    }

    @Test
    fun successfulResendRunsOnceAndRestartsCountdown() = runTest(dispatcher) {
        authRepository.otpResult = Result.success("123456")
        val viewModel = viewModel()
        viewModel.onEvent(OtpIntent.PhoneNumberChanged("+201027642749"))
        advanceUntilIdle()

        viewModel.onEvent(OtpIntent.ResendClicked)
        viewModel.onEvent(OtpIntent.ResendClicked)
        runCurrent()

        assertEquals(1, authRepository.requestCount)
        assertEquals("+201027642749", authRepository.lastRequestedPhone)
        assertEquals("123456", viewModel.state.value.otpCode)
        assertEquals(30, viewModel.state.value.remainingSeconds)
        assertFalse(viewModel.state.value.canResend)
        advanceUntilIdle()
    }

    @Test
    fun failedResendRemainsRetryableAndDoesNotRestartCountdown() = runTest(dispatcher) {
        authRepository.otpResult = Result.failure(IllegalStateException("offline"))
        val viewModel = viewModel()
        viewModel.onEvent(OtpIntent.PhoneNumberChanged("+966501234567"))
        advanceUntilIdle()

        viewModel.onEvent(OtpIntent.ResendClicked)
        runCurrent()

        assertEquals(1, authRepository.requestCount)
        assertEquals(0, viewModel.state.value.remainingSeconds)
        assertTrue(viewModel.state.value.canResend)
        assertNotNull(viewModel.state.value.errorMessage)
    }

    @Test
    fun otpStateChangesDoNotResetCountdown() = runTest(dispatcher) {
        val viewModel = viewModel()
        advanceTimeBy(5_000)
        runCurrent()
        assertEquals(25, viewModel.state.value.remainingSeconds)

        viewModel.onEvent(OtpIntent.OtpCodeChanged("1"))
        viewModel.onEvent(OtpIntent.OtpCodeChanged("12"))

        assertEquals(25, viewModel.state.value.remainingSeconds)
        advanceUntilIdle()
    }

    private fun viewModel() = OtpViewModel(
        verifyOtpUseCase = VerifyOtpUseCase(authRepository),
        getDestination = GetAuthenticatedDestinationUseCase(unusedProfileRepository()),
        requestDevOtpUseCase = RequestDevOtpUseCase(authRepository)
    )
}

private class FakeOtpAuthRepository : AuthRepository {
    var otpResult: Result<String?> = Result.success(null)
    var requestCount: Int = 0
    var lastRequestedPhone: String? = null

    override suspend fun requestDevOtp(phoneNumber: String): Result<String?> {
        requestCount += 1
        lastRequestedPhone = phoneNumber
        return otpResult
    }

    override suspend fun loginWithPhone(phoneNumber: String) = Result.success(Unit)
    override suspend fun loginWithGoogle(idToken: String) = Result.failure<com.carenest.domain.model.auth.GoogleAuthResult>(UnsupportedOperationException())
    override suspend fun verifyOtp(phoneNumber: String, otp: String, pendingToken: String?) =
        Result.failure<AuthResult>(UnsupportedOperationException())
    override suspend fun refreshToken() = Result.success(Unit)
    override suspend fun logout() = Result.success(Unit)
}

@Suppress("UNCHECKED_CAST")
private fun unusedProfileRepository(): ProfileRepository = Proxy.newProxyInstance(
    ProfileRepository::class.java.classLoader,
    arrayOf(ProfileRepository::class.java)
) { _, method, _ ->
    throw UnsupportedOperationException("Unexpected call to ${method.name}")
} as ProfileRepository
