package com.carenest.presentation.ui.auth.register

import com.carenest.domain.model.home.User
import com.carenest.domain.model.user.UserUpdate
import com.carenest.domain.repository.ProfileRepository
import com.carenest.domain.repository.UserRepository
import com.carenest.domain.usecase.user.GetAuthenticatedDestinationUseCase
import com.carenest.domain.usecase.user.ObserveCurrentUserUseCase
import com.carenest.domain.usecase.user.UpdateCurrentUserUseCase
import java.lang.reflect.Proxy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModelTest {
    private lateinit var dispatcher: TestDispatcher

    @Before
    fun setUp() {
        dispatcher = StandardTestDispatcher()
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun newRegistrationDefaultsToMaleAndCanChangeToFemale() = runTest(dispatcher) {
        assertEquals("MALE", RegisterState().gender)
        val viewModel = viewModel(User(gender = null))
        runCurrent()

        viewModel.onEvent(RegisterIntent.ConfigureMode(PersonalInformationMode.Registration))
        assertEquals("MALE", viewModel.state.value.gender)

        viewModel.onEvent(RegisterIntent.GenderChanged("FEMALE"))
        assertEquals("FEMALE", viewModel.state.value.gender)
    }

    @Test
    fun editProfilePreservesBackendFemale() = runTest(dispatcher) {
        val viewModel = viewModel(User(gender = "FEMALE"))
        runCurrent()

        viewModel.onEvent(RegisterIntent.ConfigureMode(PersonalInformationMode.EditProfile))

        assertEquals("FEMALE", viewModel.state.value.gender)
    }

    @Test
    fun editProfilePreservesBackendMale() = runTest(dispatcher) {
        val viewModel = viewModel(User(gender = "MALE"))
        runCurrent()

        viewModel.onEvent(RegisterIntent.ConfigureMode(PersonalInformationMode.EditProfile))

        assertEquals("MALE", viewModel.state.value.gender)
    }

    private fun viewModel(user: User): RegisterViewModel {
        val repository = FakeRegisterUserRepository(user)
        return RegisterViewModel(
            observeCurrentUser = ObserveCurrentUserUseCase(repository),
            updateCurrentUser = UpdateCurrentUserUseCase(repository),
            getDestination = GetAuthenticatedDestinationUseCase(unusedRegisterProfileRepository()),
            userRepository = repository
        )
    }
}

private class FakeRegisterUserRepository(initial: User) : UserRepository {
    private val currentUser = MutableStateFlow<User?>(initial)

    override fun observeCurrentUser(): Flow<User?> = currentUser
    override suspend fun refreshCurrentUser(): Result<User> = Result.success(requireNotNull(currentUser.value))
    override suspend fun uploadProfileImage(fileName: String, contentType: String, bytes: ByteArray) =
        Result.failure<String>(UnsupportedOperationException())
    override suspend fun updateCurrentUser(update: UserUpdate) =
        Result.failure<User>(UnsupportedOperationException())
    override suspend fun clearCurrentUser() {
        currentUser.value = null
    }
}

@Suppress("UNCHECKED_CAST")
private fun unusedRegisterProfileRepository(): ProfileRepository = Proxy.newProxyInstance(
    ProfileRepository::class.java.classLoader,
    arrayOf(ProfileRepository::class.java)
) { _, method, _ ->
    throw UnsupportedOperationException("Unexpected call to ${method.name}")
} as ProfileRepository
