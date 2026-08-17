package com.carenest.data.repository

import com.carenest.data.source.remote.ApiException
import com.carenest.data.source.remote.datasource.WalletRemoteDataSource
import com.carenest.data.source.remote.dto.wallet.CreditUpdateRequestDto
import com.carenest.domain.model.payment.WalletCredit
import com.carenest.domain.model.payment.WalletException
import com.carenest.domain.model.payment.WalletOperation
import com.carenest.domain.model.payment.WalletTopUpAmount
import com.carenest.domain.repository.UserRepository
import com.carenest.domain.repository.WalletRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first

@Singleton
class WalletRepositoryImpl @Inject constructor(
    private val remote: WalletRemoteDataSource,
    private val userRepository: UserRepository,
) : WalletRepository {
    override suspend fun getCredit(): Result<WalletCredit> =
        runCatching { currentUserId() }
            .fold(
                onSuccess = { userId ->
                    remote.getCredit(userId).map { WalletCredit(it.credit) }.walletFailure()
                },
                onFailure = { Result.failure(it.toWalletFailure()) },
            )

    override suspend fun updateCredit(
        amount: WalletTopUpAmount,
        operation: WalletOperation,
    ): Result<WalletCredit> {
        return runCatching { currentUserId() }
            .fold(
                onSuccess = { userId ->
                    remote.updateCredit(
                        userId,
                        CreditUpdateRequestDto(
                            amount = amount.decimal,
                            operation = operation.backendValue,
                        ),
                    ).map { WalletCredit(it.credit) }.walletFailure()
                },
                onFailure = { Result.failure(it.toWalletFailure()) },
            )
    }

    private suspend fun currentUserId(): String {
        val cachedId = userRepository.observeCurrentUser().first()?.id
        if (!cachedId.isNullOrBlank()) return cachedId

        val refreshedId = userRepository.refreshCurrentUser().getOrThrow().id
        if (refreshedId.isNotBlank()) return refreshedId

        throw WalletException.MissingAuthenticatedUserId
    }
}

private fun Throwable.toWalletFailure(): Throwable = when (this) {
    is WalletException -> this
    is ApiException -> {
        val code = backendCode.orEmpty()
        val body = message.orEmpty()
        if (
            code.equals("INSUFFICIENT_CREDIT", ignoreCase = true) ||
            body.contains("INSUFFICIENT_CREDIT", ignoreCase = true) ||
            body.contains("insufficient", ignoreCase = true)
        ) {
            WalletException.InsufficientCredit
        } else {
            this
        }
    }
    else -> this
}

private fun <T> Result<T>.walletFailure(): Result<T> =
    exceptionOrNull()?.let { Result.failure(it.toWalletFailure()) } ?: this
