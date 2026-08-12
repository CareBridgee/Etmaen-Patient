package com.carenest.presentation.ui.profile

import com.carenest.domain.model.home.User
import com.carenest.domain.model.auth.AuthResult
import com.carenest.domain.model.profile.*
import com.carenest.domain.repository.AuthRepository
import com.carenest.domain.repository.ProfileRepository
import com.carenest.domain.repository.UserRepository
import com.carenest.domain.usecase.auth.LogoutUseCase
import com.carenest.domain.usecase.profile.GetDefaultProfileUseCase
import com.carenest.domain.usecase.profile.GetProfilesUseCase
import com.carenest.domain.usecase.profile.UpdateProfileAvatarUseCase
import com.carenest.domain.usecase.user.GetCurrentUserUseCase
import com.carenest.domain.usecase.user.ObserveCurrentUserUseCase
import com.carenest.domain.model.user.UserUpdate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {
    private lateinit var users: FakeUserRepository
    private lateinit var profiles: FakeProfileRepository
    private lateinit var auth: FakeAuthRepository

    @Before fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        users = FakeUserRepository(user("user-1", "profile-1", "Alaa"))
        profiles = FakeProfileRepository(profile("profile-1", "Alaa"), listOf(
            profile("profile-1", "Alaa"), profile("dependent-1", "Mona")
        ))
        auth = FakeAuthRepository()
    }

    @After fun tearDown() = Dispatchers.resetMain()

    @Test fun initialLoad_populatesAuthenticatedAccountData() = runTest {
        val viewModel = viewModel()
        assertEquals("Alaa Adel", viewModel.state.value.userName)
        assertEquals("https://example.com/al.png", viewModel.state.value.userAvatarUrl)
        assertEquals(1, viewModel.state.value.activeDependentsCount)
        assertEquals("profile-1", viewModel.state.value.profile?.id)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test fun initialFailure_isRetryable() = runTest {
        users.failure = true
        val viewModel = viewModel()
        assertNotNull(viewModel.state.value.errorMessage)
        users.failure = false
        viewModel.onEvent(ProfileEvent.OnRetryClicked)
        assertEquals("profile-1", viewModel.state.value.profile?.id)
        assertNull(viewModel.state.value.errorMessage)
    }

    @Test fun incompleteProfile_isHandledSafely() = runTest {
        users.remote = user("user-1", "profile-1", null).copy(profileImageUrl = null)
        profiles.default = profile("profile-1", null).copy(relationship = null)
        profiles.items = listOf(profiles.default)
        val state = viewModel().state.value
        assertEquals("", state.userName)
        assertEquals("", state.userRole)
        assertNull(state.userAvatarUrl)
        assertEquals(0, state.activeDependentsCount)
    }

    @Test fun refresh_replacesPreviousAccountData() = runTest {
        val viewModel = viewModel()
        users.remote = user("user-2", "profile-2", "Nour").copy(profileImageUrl = null)
        profiles.default = profile("profile-2", "Nour")
        profiles.items = listOf(profiles.default)
        viewModel.onEvent(ProfileEvent.OnRefreshProfile)
        assertEquals("Nour Adel", viewModel.state.value.userName)
        assertEquals("profile-2", viewModel.state.value.profile?.id)
        assertEquals(0, viewModel.state.value.activeDependentsCount)
    }

    @Test fun selectedAvatar_isUploadedAndApplied() = runTest {
        val viewModel = viewModel()
        viewModel.onEvent(
            ProfileEvent.OnAvatarSelected(
                fileName = "avatar.png",
                contentType = "image/png",
                bytes = byteArrayOf(1, 2, 3)
            )
        )
        assertEquals("https://example.com/new-avatar.jpg", viewModel.state.value.userAvatarUrl)
        assertFalse(viewModel.state.value.isUpdatingAvatar)
    }

    @Test fun everyMenuEvent_emitsItsNavigationEffect() = runTest {
        val viewModel = viewModel()
        val cases = listOf(
            ProfileEvent.OnPersonalInfoClicked to ProfileEffect.NavigateToPersonalInfo,
            ProfileEvent.OnHealthProfileClicked to ProfileEffect.NavigateToHealthProfile,
            ProfileEvent.OnFamilyMembersClicked to ProfileEffect.NavigateToFamilyMembers,
            ProfileEvent.OnAddressesClicked to ProfileEffect.NavigateToAddresses,
            ProfileEvent.OnPaymentClicked to ProfileEffect.NavigateToPayment,
            ProfileEvent.OnSettingsClicked to ProfileEffect.NavigateToSettings
        )
        cases.forEach { (event, expected) ->
            viewModel.onEvent(event)
            assertEquals(expected, viewModel.effect.first())
        }
    }

    @Test fun successfulLogout_navigatesToLogin() = runTest {
        val viewModel = viewModel()
        viewModel.onEvent(ProfileEvent.OnLogoutClicked)
        assertEquals(ProfileEffect.NavigateToLogout, viewModel.effect.first())
    }

    @Test fun failedLogout_staysOnProfileAndCanRetry() = runTest {
        auth.failure = true
        val viewModel = viewModel()
        viewModel.onEvent(ProfileEvent.OnLogoutClicked)
        assertEquals(ProfileEffect.ShowLogoutError, viewModel.effect.first())
        assertFalse(viewModel.state.value.isLoggingOut)
    }

    private fun viewModel() = ProfileViewModel(
        GetCurrentUserUseCase(users),
        ObserveCurrentUserUseCase(users),
        GetDefaultProfileUseCase(profiles, users),
        GetProfilesUseCase(profiles),
        UpdateProfileAvatarUseCase(users),
        LogoutUseCase(auth)
    )
}

private class FakeUserRepository(initial: User) : UserRepository {
    private val local = MutableStateFlow<User?>(initial)
    var remote = initial
    var failure = false
    override fun observeCurrentUser(): Flow<User?> = local
    override suspend fun refreshCurrentUser(): Result<User> = if (failure) {
        Result.failure(IllegalStateException("offline"))
    } else Result.success(remote).onSuccess { local.value = it }
    override suspend fun uploadProfileImage(fileName: String, contentType: String, bytes: ByteArray) =
        if (failure) Result.failure(IllegalStateException("offline"))
        else Result.success("https://example.com/new-avatar.jpg")
    override suspend fun updateCurrentUser(update: UserUpdate): Result<User> {
        remote = remote.copy(profileImageUrl = update.profileImageUrl)
        local.value = remote
        return Result.success(remote)
    }
    override suspend fun clearCurrentUser() { local.value = null }
}

private class FakeAuthRepository : AuthRepository {
    var failure = false
    override suspend fun loginWithPhone(phoneNumber: String) = Result.success(Unit)
    override suspend fun requestDevOtp(phoneNumber: String) = Result.success(null)
    override suspend fun verifyOtp(phoneNumber: String, otp: String) = unsupported<AuthResult>()
    override suspend fun refreshToken(): Result<Unit> = Result.success(Unit)
    override suspend fun logout() = if (failure) Result.failure(IllegalStateException()) else Result.success(Unit)
}

private class FakeProfileRepository(
    var default: Profile,
    var items: List<Profile>
) : ProfileRepository {
    override suspend fun getProfile(profileId: String) =
        items.firstOrNull { it.id == profileId }?.let(Result.Companion::success)
            ?: Result.failure(IllegalStateException())
    override suspend fun getDefaultProfile() = Result.success(default)
    override suspend fun getProfiles() = Result.success(items)
    override suspend fun getProfileMedications(profileId: String) = unsupported<List<ProfileMedication>>()
    override suspend fun syncProfileMedications(profileId: String, names: List<String>) = unsupported<List<String>>()
    override suspend fun createFamilyMember(relationship: String, firstName: String, lastName: String, dateOfBirth: String, gender: String) = unsupported<Profile>()
    override suspend fun updatePersonalInfo(profileId: String, update: PersonalInfoUpdate) = unsupported<Profile>()
    override suspend fun updateBasicHealth(profileId: String, update: BasicHealthUpdate) = unsupported<Profile>()
    override suspend fun updateMedicalHistory(profileId: String, update: MedicalHistoryUpdate) = unsupported<Profile>()
    override suspend fun updateMobility(profileId: String, mobilityStatus: String, mobilityNotes: String) = unsupported<Profile>()
    override suspend fun getMedicalConditionCatalog() = unsupported<List<MedicalCondition>>()
    override suspend fun getProfileMedicalConditions(profileId: String) = unsupported<List<ProfileMedicalCondition>>()
    override suspend fun syncProfileMedicalConditions(profileId: String, originalBackendIds: Set<String>, selectedBackendIds: Set<String>) = unsupported<Set<String>>()
    override suspend fun syncProfileMedicalConditionsByName(profileId: String, names: List<String>) = unsupported<Unit>()
    override suspend fun addCustomMedicalCondition(profileId: String, name: String) = unsupported<ProfileMedicalCondition>()
    override suspend fun getAllergyCatalog() = unsupported<List<Allergy>>()
    override suspend fun getProfileAllergies(profileId: String) = unsupported<List<ProfileAllergy>>()
    override suspend fun syncProfileAllergies(profileId: String, originalBackendIds: Set<String>, selectedBackendIds: Set<String>) = unsupported<Set<String>>()
    override suspend fun syncProfileAllergiesByName(profileId: String, names: List<String>) = unsupported<Unit>()
    override suspend fun addCustomAllergy(profileId: String, name: String) = unsupported<ProfileAllergy>()
    override suspend fun getEmergencyContacts(profileId: String) = unsupported<List<EmergencyContact>>()
    override suspend fun getEmergencyContactById(emergencyContactId: String) = unsupported<EmergencyContact>()
    override suspend fun createEmergencyContact(profileId: String, input: EmergencyContactInput) = unsupported<EmergencyContact>()
    override suspend fun updateEmergencyContact(emergencyContactId: String, input: EmergencyContactInput) = unsupported<EmergencyContact>()
    override suspend fun deleteEmergencyContact(emergencyContactId: String) = unsupported<Unit>()
    override suspend fun getProfileReport(profileId: String) = unsupported<String>()
}

private fun user(id: String, profileId: String, firstName: String?) = User(
    id = id, firstName = firstName, lastName = firstName?.let { "Adel" },
    profileImageUrl = "https://example.com/al.png", defaultProfileId = profileId
)

private fun profile(id: String, firstName: String?) = Profile(
    id, "user", "SELF", firstName, firstName?.let { "Adel" }, "1990-01-01", "MALE",
    null, null, null, null, null, null, null, isPrimary = id.startsWith("profile")
)

private fun <T> unsupported(): Result<T> = Result.failure(UnsupportedOperationException())
