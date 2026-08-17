package com.carenest.data.repository

import com.carenest.data.source.remote.ApiException
import com.carenest.data.source.remote.datasource.WalletRemoteDataSource
import com.carenest.data.source.remote.dto.wallet.CreditResponseDto
import com.carenest.data.source.remote.dto.wallet.CreditUpdateRequestDto
import com.carenest.data.source.remote.dto.wallet.CreditUpdateResponseDto
import com.carenest.domain.model.home.User
import com.carenest.domain.model.payment.WalletException
import com.carenest.domain.model.payment.WalletOperation
import com.carenest.domain.model.payment.WalletTopUpAmount
import com.carenest.domain.model.user.UserUpdate
import com.carenest.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WalletRepositoryImplTest {
    @Test
    fun `get credit uses authenticated user id`() = runTest {
        val remote = RecordingWalletRemoteDataSource()
        val users = FakeWalletUserRepository(
            User(id = "user-id", defaultProfileId = "profile-id"),
        )
        val repository = WalletRepositoryImpl(remote, users)

        val credit = repository.getCredit().getOrThrow()

        assertEquals("user-id", remote.lastGetUserId)
        assertEquals(250.0, credit.credit, 0.0)
    }

    @Test
    fun `update credit sends positive amount and backend operation`() = runTest {
        val remote = RecordingWalletRemoteDataSource()
        val users = FakeWalletUserRepository(
            User(id = "user-id", defaultProfileId = "profile-id"),
        )
        val repository = WalletRepositoryImpl(remote, users)

        val credit = repository.updateCredit(
            WalletTopUpAmount.parse("500.00").getOrThrow(),
            WalletOperation.Add,
        ).getOrThrow()

        assertEquals("user-id", remote.lastPatchUserId)
        assertEquals("500.00", remote.lastPatchRequest?.amount?.toPlainString())
        assertEquals("ADD", remote.lastPatchRequest?.operation)
        assertEquals(750.0, credit.credit, 0.0)
    }

    @Test
    fun `invalid amount is rejected before repository call`() {
        val result = WalletTopUpAmount.parse("-10.0")

        assertTrue(result.exceptionOrNull() is WalletException.InvalidAmount)
    }

    @Test
    fun `insufficient credit backend code maps to wallet failure`() = runTest {
        val remote = RecordingWalletRemoteDataSource().apply {
            updateFailure = ApiException(
                statusCode = 400,
                backendCode = "INSUFFICIENT_CREDIT",
                message = "INSUFFICIENT_CREDIT",
            )
        }
        val users = FakeWalletUserRepository(User(id = "user-id"))
        val repository = WalletRepositoryImpl(remote, users)

        val result = repository.updateCredit(
            WalletTopUpAmount.parse("200.00").getOrThrow(),
            WalletOperation.Deduct,
        )

        assertTrue(result.exceptionOrNull() is WalletException.InsufficientCredit)
    }
}

private class RecordingWalletRemoteDataSource : WalletRemoteDataSource {
    var lastGetUserId: String? = null
    var lastPatchUserId: String? = null
    var lastPatchRequest: CreditUpdateRequestDto? = null
    var updateFailure: Throwable? = null

    override suspend fun getCredit(userId: String): Result<CreditResponseDto> {
        lastGetUserId = userId
        return Result.success(CreditResponseDto(250.0))
    }

    override suspend fun updateCredit(
        userId: String,
        request: CreditUpdateRequestDto,
    ): Result<CreditUpdateResponseDto> {
        lastPatchUserId = userId
        lastPatchRequest = request
        updateFailure?.let { return Result.failure(it) }
        return Result.success(CreditUpdateResponseDto(750.0))
    }
}

private class FakeWalletUserRepository(initial: User?) : UserRepository {
    private val current = MutableStateFlow(initial)

    override fun observeCurrentUser(): Flow<User?> = current

    override suspend fun refreshCurrentUser(): Result<User> =
        current.value?.let(Result.Companion::success)
            ?: Result.failure(WalletException.MissingAuthenticatedUserId)

    override suspend fun uploadProfileImage(
        fileName: String,
        contentType: String,
        bytes: ByteArray,
    ): Result<String> = Result.failure(UnsupportedOperationException())

    override suspend fun updateCurrentUser(update: UserUpdate): Result<User> =
        Result.failure(UnsupportedOperationException())

    override suspend fun clearCurrentUser() {
        current.value = null
    }
}
