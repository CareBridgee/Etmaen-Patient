package com.carenest.data.repository

import com.carenest.data.mapper.user.toDomain
import com.carenest.data.mapper.user.toDto
import com.carenest.data.mapper.user.toEntity
import com.carenest.data.source.local.datasource.UserLocalDataSource
import com.carenest.data.source.remote.ApiException
import com.carenest.data.source.remote.datasource.user.UserRemoteDataSource
import com.carenest.domain.model.home.User
import com.carenest.domain.model.user.UserException
import com.carenest.domain.model.user.UserUpdate
import com.carenest.domain.repository.UserRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepositoryImpl @Inject constructor(
    private val remote: UserRemoteDataSource,
    private val local: UserLocalDataSource
) : UserRepository {
    override fun observeCurrentUser(): Flow<User?> =
        local.observeCurrentUser().map { it?.toDomain() }

    override suspend fun refreshCurrentUser(): Result<User> =
        sync(remote.getCurrentUser())

    override suspend fun updateCurrentUser(update: UserUpdate): Result<User> =
        sync(remote.updateCurrentUser(update.toDto()))

    override suspend fun clearCurrentUser() = local.clearCurrentUser()

    private suspend fun sync(remoteResult: Result<com.carenest.data.source.remote.dto.user.UserResponseDto>): Result<User> =
        remoteResult
            .mapCatching { it.toDomain() }
            .onSuccess { local.upsertCurrentUser(it.toEntity()) }
            .userFailure()
}

private fun Throwable.toUserFailure(): Throwable = when (this) {
    is UserException -> this
    is ApiException -> UserException(message ?: "User request failed", statusCode, backendCode)
    else -> this
}

private fun <T> Result<T>.userFailure(): Result<T> =
    exceptionOrNull()?.let { Result.failure(it.toUserFailure()) } ?: this
