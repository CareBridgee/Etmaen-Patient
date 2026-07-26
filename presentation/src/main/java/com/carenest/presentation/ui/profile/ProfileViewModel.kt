package com.carenest.presentation.ui.profile

import androidx.lifecycle.ViewModel
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor() :
    ViewModel(),
    StateHolder<ProfileState> by DefaultStateHolder(ProfileState()),
    EffectPublisher<ProfileEffect> by DefaultEffectPublisher() {

    fun onEvent(event: ProfileEvent) {
        when (event) {
            ProfileEvent.OnPersonalInfoClicked -> {
                sendEffect(ProfileEffect.NavigateToPersonalInfo)
            }
            ProfileEvent.OnHealthProfileClicked -> {
                sendEffect(ProfileEffect.NavigateToHealthProfile)
            }
            ProfileEvent.OnFamilyMembersClicked -> {
                sendEffect(ProfileEffect.NavigateToFamilyMembers)
            }
            ProfileEvent.OnAddressesClicked -> {
                sendEffect(ProfileEffect.NavigateToAddresses)
            }
            ProfileEvent.OnPaymentClicked -> {
                sendEffect(ProfileEffect.NavigateToPayment)
            }
            ProfileEvent.OnSettingsClicked -> {
                sendEffect(ProfileEffect.NavigateToSettings)
            }
            ProfileEvent.OnLogoutClicked -> {
                sendEffect(ProfileEffect.NavigateToLogout)
            }
            ProfileEvent.OnEditAvatarClicked -> {
                // Future avatar edit action
            }
            ProfileEvent.OnNotificationClicked -> {
                sendEffect(ProfileEffect.ShowNotification)
            }
        }
    }
}
