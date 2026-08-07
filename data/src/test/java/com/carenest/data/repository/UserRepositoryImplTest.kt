package com.carenest.data.repository

import com.carenest.data.source.local.database.entity.UserEntity
import com.carenest.data.source.local.datasource.UserLocalDataSource
import com.carenest.data.source.remote.datasource.user.UserRemoteDataSource
import com.carenest.data.source.remote.dto.user.UpdateUserRequestDto
import com.carenest.data.source.remote.dto.user.UserResponseDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import com.carenest.domain.model.user.UserUpdate

class UserRepositoryImplTest {
    @Test
    fun `refresh synchronizes remote user into local source of truth`() = runTest {
        val local = FakeLocalDataSource()
        val remote = FakeRemoteDataSource(
            UserResponseDto(
                id = "user-id",
                phoneNumber = "+201000000000",
                defaultProfileId = "profile-id"
            )
        )
        val repository = UserRepositoryImpl(
            remote = remote,
            local = local
        )

        val refreshed = repository.refreshCurrentUser().getOrThrow()
        val observed = repository.observeCurrentUser().first()

        assertEquals("user-id", refreshed.id)
        assertEquals(refreshed, observed)
        assertEquals(1, local.upsertCount)
    }

    @Test
    fun `update sends request and upserts returned user`() = runTest {
        val local = FakeLocalDataSource()
        val remote = FakeRemoteDataSource(
            UserResponseDto(
                id = "user-id",
                phoneNumber = "+201000000000",
                firstName = "Aalaa",
                lastName = "Adel"
            )
        )
        val repository = UserRepositoryImpl(remote, local)

        repository.updateCurrentUser(
            UserUpdate(firstName = "Aalaa", lastName = "Adel")
        ).getOrThrow()

        assertEquals("Aalaa", remote.lastUpdate?.firstName)
        assertEquals("Adel", repository.observeCurrentUser().first()?.lastName)
        assertEquals(1, local.upsertCount)
    }
}

private class FakeRemoteDataSource(
    private val response: UserResponseDto
) : UserRemoteDataSource {
    var lastUpdate: UpdateUserRequestDto? = null

    override suspend fun getCurrentUser() = Result.success(response)

    override suspend fun uploadProfileImage(
        fileName: String,
        contentType: String,
        bytes: ByteArray
    ) = Result.success("https://example.com/profile.jpg")

    override suspend fun updateCurrentUser(request: UpdateUserRequestDto): Result<UserResponseDto> {
        lastUpdate = request
        return Result.success(response)
    }
}

private class FakeLocalDataSource : UserLocalDataSource {
    private val user = MutableStateFlow<UserEntity?>(null)
    var upsertCount = 0
        private set

    override fun observeCurrentUser(): Flow<UserEntity?> = user

    override suspend fun upsertCurrentUser(user: UserEntity) {
        upsertCount++
        this.user.value = user
    }

    override suspend fun clearCurrentUser() {
        user.value = null
    }
}
