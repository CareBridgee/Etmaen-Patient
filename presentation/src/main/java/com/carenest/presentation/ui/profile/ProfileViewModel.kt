package com.carenest.presentation.ui.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.domain.usecase.auth.LogoutUseCase
import com.carenest.domain.usecase.profile.GetDefaultProfileUseCase
import com.carenest.domain.usecase.profile.GetProfilesUseCase
import com.carenest.domain.usecase.profile.UpdateProfileAvatarUseCase
import com.carenest.domain.usecase.user.GetCurrentUserUseCase
import com.carenest.domain.usecase.user.ObserveCurrentUserUseCase
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import javax.inject.Inject
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getCurrentUser: GetCurrentUserUseCase,
    private val observeCurrentUser: ObserveCurrentUserUseCase,
    private val getDefaultProfile: GetDefaultProfileUseCase,
    private val getProfiles: GetProfilesUseCase,
    private val updateProfileAvatar: UpdateProfileAvatarUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel(),
    StateHolder<ProfileState> by DefaultStateHolder(ProfileState()),
    EffectPublisher<ProfileEffect> by DefaultEffectPublisher() {

    init {
        observeUser()
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
            ProfileEvent.OnLogoutClicked -> performLogout()
            ProfileEvent.OnEditAvatarClicked -> {
                if (!currentState.isUpdatingAvatar) sendEffect(ProfileEffect.SelectAvatar)
            }
            is ProfileEvent.OnAvatarSelected -> updateAvatar(event)
            ProfileEvent.OnRetryClicked -> loadProfile(refresh = false)
            ProfileEvent.OnRefreshProfile -> loadProfile(refresh = currentState.profile != null)
        }
    }

    private fun observeUser() {
        viewModelScope.launch {
            observeCurrentUser().collect { user ->
                updateState {
                    copy(
                        userName = user?.name.orEmpty(),
                        userAvatarUrl = user?.profileImageUrl?.takeIf(String::isNotBlank)
                    )
                }
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
                userRole = if (refresh) userRole else "",
                activeDependentsCount = if (refresh) activeDependentsCount else 0
            )
        }
        viewModelScope.launch {
            val currentUser = getCurrentUser().getOrElse {
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
            val profileName = listOfNotNull(profile.firstName, profile.lastName).filter { it.isNotBlank() }.joinToString(" ")
            val currentUserName = currentUser.name.orEmpty()
            val resolvedName = currentUserName.ifBlank { profileName }

            updateState {
                copy(
                    profile = profile,
                    userName = if (userName.isBlank() || userName.equals("User", ignoreCase = true)) resolvedName else userName,
                    userRole = profile.relationship.orEmpty(),
                    greeting = currentGreeting(),
                    activeDependentsCount = profiles.count { !it.isDeleted && it.id != profile.id },
                    isLoading = false,
                    isRefreshing = false,
                    errorMessage = null
                )
            }
        }
    }

    private fun performLogout() {
        if (currentState.isLoggingOut) return
        updateState { copy(isLoggingOut = true, errorMessage = null) }
        viewModelScope.launch {
            logoutUseCase.invoke().fold(
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
                onSuccess = {
                    updateState {
                        copy(
                            isUpdatingAvatar = false
                        )
                    }
                    sendEffect(ProfileEffect.ShowAvatarUpdated)
                },
                onFailure = { error ->
                    Log.e("ProfileViewModel", "Avatar update failed", error)
                    updateState { copy(isUpdatingAvatar = false) }
                    sendEffect(ProfileEffect.ShowAvatarUpdateFailed(error.message ?: error.toString()))
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

    private fun currentGreeting(): ProfileGreeting = when (
        Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    ) {
        in 5..11 -> ProfileGreeting.Morning
        in 12..17 -> ProfileGreeting.Day
        else -> ProfileGreeting.Evening
    }

    companion object {
        private const val PROFILE_LOAD_ERROR = "profile_load_failed"
    }
}
