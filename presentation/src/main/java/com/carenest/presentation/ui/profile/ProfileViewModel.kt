package com.carenest.presentation.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.domain.usecase.auth.LogoutUseCase
import com.carenest.domain.usecase.profile.GetDefaultProfileUseCase
import com.carenest.domain.usecase.profile.GetProfilesUseCase
import com.carenest.domain.usecase.profile.UpdateProfileAvatarUseCase
import com.carenest.domain.usecase.user.GetCurrentUserUseCase
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalTime
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getCurrentUser: GetCurrentUserUseCase,
    private val getDefaultProfile: GetDefaultProfileUseCase,
    private val getProfiles: GetProfilesUseCase,
    private val updateProfileAvatar: UpdateProfileAvatarUseCase,
    private val logout: LogoutUseCase
) : ViewModel(),
    StateHolder<ProfileState> by DefaultStateHolder(ProfileState()),
    EffectPublisher<ProfileEffect> by DefaultEffectPublisher() {

    init {
        loadProfile(refresh = false)
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {
            ProfileEvent.OnPersonalInfoClicked -> sendEffect(ProfileEffect.NavigateToPersonalInfo)
            ProfileEvent.OnHealthProfileClicked -> sendEffect(ProfileEffect.NavigateToHealthProfile)
            ProfileEvent.OnFamilyMembersClicked -> sendEffect(ProfileEffect.NavigateToFamilyMembers)
            ProfileEvent.OnAddressesClicked -> sendEffect(ProfileEffect.NavigateToAddresses)
            ProfileEvent.OnPaymentClicked -> sendEffect(ProfileEffect.NavigateToPayment)
            ProfileEvent.OnSettingsClicked -> sendEffect(ProfileEffect.NavigateToSettings)
            ProfileEvent.OnLogoutClicked -> logout()
            ProfileEvent.OnEditAvatarClicked -> {
                if (!currentState.isUpdatingAvatar) sendEffect(ProfileEffect.SelectAvatar)
            }
            is ProfileEvent.OnAvatarSelected -> updateAvatar(event)
            ProfileEvent.OnNotificationClicked -> sendEffect(ProfileEffect.ShowNotificationsUnavailable)
            ProfileEvent.OnRetryClicked -> loadProfile(refresh = false)
            ProfileEvent.OnRefreshProfile -> loadProfile(refresh = currentState.profile != null)
            is ProfileEvent.OnAppVersionAvailable -> updateState {
                copy(appVersion = event.versionName.trim())
            }
        }
    }

    private fun loadProfile(refresh: Boolean) {
        if (currentState.isLoading || currentState.isRefreshing) return
        updateState {
            copy(
                isLoading = !refresh,
                isRefreshing = refresh,
                errorMessage = null,
                profile = if (refresh) profile else null,
                userName = if (refresh) userName else "",
                userRole = if (refresh) userRole else "",
                userAvatarUrl = if (refresh) userAvatarUrl else null,
                activeDependentsCount = if (refresh) activeDependentsCount else 0
            )
        }
        viewModelScope.launch {
            val user = getCurrentUser().getOrElse {
                failLoading(refresh)
                return@launch
            }
            val profile = getDefaultProfile().getOrElse {
                failLoading(refresh)
                return@launch
            }
            val profiles = getProfiles().getOrElse {
                failLoading(refresh)
                return@launch
            }
            val profileName = listOfNotNull(profile.firstName, profile.lastName)
                .joinToString(" ").trim()
            updateState {
                copy(
                    profile = profile,
                    userName = user.name ?: profileName,
                    userRole = profile.relationship.orEmpty(),
                    userAvatarUrl = user.profileImageUrl?.takeIf(String::isNotBlank),
                    greeting = currentGreeting(),
                    activeDependentsCount = profiles.count { !it.isDeleted && it.id != profile.id },
                    isLoading = false,
                    isRefreshing = false,
                    errorMessage = null
                )
            }
        }
    }

    private fun logout() {
        if (currentState.isLoggingOut) return
        updateState { copy(isLoggingOut = true, errorMessage = null) }
        viewModelScope.launch {
            logout.invoke().fold(
                onSuccess = { sendEffect(ProfileEffect.NavigateToLogout) },
                onFailure = {
                    updateState { copy(isLoggingOut = false) }
                    sendEffect(ProfileEffect.ShowLogoutError)
                }
            )
        }
    }

    private fun updateAvatar(event: ProfileEvent.OnAvatarSelected) {
        if (currentState.isUpdatingAvatar) return
        updateState { copy(isUpdatingAvatar = true) }
        viewModelScope.launch {
            updateProfileAvatar(event.fileName, event.contentType, event.bytes).fold(
                onSuccess = { user ->
                    updateState {
                        copy(
                            isUpdatingAvatar = false,
                            userAvatarUrl = user.profileImageUrl?.takeIf(String::isNotBlank)
                        )
                    }
                    sendEffect(ProfileEffect.ShowAvatarUpdated)
                },
                onFailure = {
                    updateState { copy(isUpdatingAvatar = false) }
                    sendEffect(ProfileEffect.ShowAvatarUpdateFailed)
                }
            )
        }
    }

    private fun failLoading(refresh: Boolean) = updateState {
        if (refresh && profile != null) sendEffect(ProfileEffect.ShowProfileRefreshError)
        copy(
            isLoading = false,
            isRefreshing = false,
            errorMessage = if (refresh && profile != null) null else PROFILE_LOAD_ERROR
        )
    }

    private fun currentGreeting(): ProfileGreeting = when (LocalTime.now().hour) {
        in 5..11 -> ProfileGreeting.Morning
        in 12..17 -> ProfileGreeting.Day
        else -> ProfileGreeting.Evening
    }

    private companion object {
        const val PROFILE_LOAD_ERROR = "profile_load_failed"
    }
}
