package com.carenest.domain.usecase.user

import com.carenest.domain.model.home.User
import com.carenest.domain.model.user.UserUpdate
import com.carenest.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class CurrentUserUseCasesTest {
    @Test
    fun `get current user delegates remote refresh`() = runTest {
        val repository = FakeUserRepository()

        val result = GetCurrentUserUseCase(repository)().getOrThrow()

        assertEquals("user-id", result.id)
        assertEquals(1, repository.refreshCount)
    }

    @Test
    fun `update current user validates and maps date`() = runTest {
        val repository = FakeUserRepository()

        UpdateCurrentUserUseCase(repository)(
            firstName = "Aalaa",
            lastName = "Adel",
            dateOfBirth = "01/02/1990",
            gender = "FEMALE"
        ).getOrThrow()

        assertEquals("1990-01-02", repository.lastUpdate?.dateOfBirth)
        assertEquals("Aalaa", repository.lastUpdate?.firstName)
    }
}

private class FakeUserRepository : UserRepository {
    private val user = User(id = "user-id", phoneNumber = "+201000000000")
    private val current = MutableStateFlow<User?>(user)
    var refreshCount = 0
    var lastUpdate: UserUpdate? = null

    override fun observeCurrentUser(): Flow<User?> = current

    override suspend fun refreshCurrentUser(): Result<User> {
        refreshCount++
        return Result.success(user)
    }

    override suspend fun updateCurrentUser(update: UserUpdate): Result<User> {
        lastUpdate = update
        return Result.success(user)
    }

    override suspend fun clearCurrentUser() {
        current.value = null
    }
}
