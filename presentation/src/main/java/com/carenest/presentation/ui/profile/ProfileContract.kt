package com.carenest.presentation.ui.profile

data class ProfileState(
    val userName: String = "Elena Rodriguez",
    val userRole: String = "Primary Caregiver",
    val userAvatarUrl: String? = null,
    val greeting: String = "Good morning, Elena",
    val activeDependentsCount: Int = 2,
    val paymentMethodInfo: String = "Visa ending in ••42",
    val appVersion: String = "Serene Care v2.4.1",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

sealed interface ProfileEvent {
    data object OnPersonalInfoClicked : ProfileEvent
    data object OnHealthProfileClicked : ProfileEvent
    data object OnFamilyMembersClicked : ProfileEvent
    data object OnAddressesClicked : ProfileEvent
    data object OnPaymentClicked : ProfileEvent
    data object OnSettingsClicked : ProfileEvent
    data object OnLogoutClicked : ProfileEvent
    data object OnEditAvatarClicked : ProfileEvent
    data object OnNotificationClicked : ProfileEvent
}

sealed interface ProfileEffect {
    data object NavigateToPersonalInfo : ProfileEffect
    data object NavigateToHealthProfile : ProfileEffect
    data object NavigateToFamilyMembers : ProfileEffect
    data object NavigateToAddresses : ProfileEffect
    data object NavigateToPayment : ProfileEffect
    data object NavigateToSettings : ProfileEffect
    data object NavigateToLogout : ProfileEffect
    data object ShowNotification : ProfileEffect
}
