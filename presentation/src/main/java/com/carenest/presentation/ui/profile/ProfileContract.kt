package com.carenest.presentation.ui.profile

import com.carenest.domain.model.profile.Profile

data class ProfileState(
    val profile: Profile? = null,
    val userName: String = "",
    val userRole: String = "",
    val userAvatarUrl: String? = null,
    val greeting: ProfileGreeting = ProfileGreeting.Day,
    val activeDependentsCount: Int = 0,
    val paymentMethodInfo: String? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isUpdatingAvatar: Boolean = false,
    val isLoggingOut: Boolean = false,
    val errorMessage: String? = null,
)

enum class ProfileGreeting { Morning, Day, Evening }

sealed interface ProfileEvent {
    data object OnPersonalInfoClicked : ProfileEvent
    data object OnHealthProfileClicked : ProfileEvent
    data object OnFamilyMembersClicked : ProfileEvent
    data object OnAddressesClicked : ProfileEvent
    data object OnPaymentClicked : ProfileEvent
    data object OnSettingsClicked : ProfileEvent
    data object OnLogoutClicked : ProfileEvent
    data object OnEditAvatarClicked : ProfileEvent
    data class OnAvatarSelected(
        val fileName: String,
        val contentType: String,
        val bytes: ByteArray
    ) : ProfileEvent
    data object OnRetryClicked : ProfileEvent
    data object OnRefreshProfile : ProfileEvent
}

sealed interface ProfileEffect {
    data object NavigateToPersonalInfo : ProfileEffect
    data object NavigateToHealthProfile : ProfileEffect
    data object NavigateToFamilyMembers : ProfileEffect
    data object NavigateToAddresses : ProfileEffect
    data object NavigateToPayment : ProfileEffect
    data object NavigateToSettings : ProfileEffect
    data object NavigateToLogout : ProfileEffect
    data object SelectAvatar : ProfileEffect
    data object ShowAvatarUpdated : ProfileEffect
    data object ShowAvatarUpdateFailed : ProfileEffect
    data object ShowProfileRefreshError : ProfileEffect
    data object ShowLogoutError : ProfileEffect
}
