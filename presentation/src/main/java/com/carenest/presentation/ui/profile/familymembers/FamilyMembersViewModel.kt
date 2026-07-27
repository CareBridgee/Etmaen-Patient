package com.carenest.presentation.ui.profile.familymembers

import androidx.lifecycle.ViewModel
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FamilyMembersViewModel @Inject constructor() :
    ViewModel(),
    StateHolder<FamilyMembersState> by DefaultStateHolder(FamilyMembersState()),
    EffectPublisher<FamilyMembersEffect> by DefaultEffectPublisher() {

    fun onEvent(event: FamilyMembersEvent) {
        when (event) {
            FamilyMembersEvent.OnBackClicked -> {
                sendEffect(FamilyMembersEffect.NavigateBack)
            }
            FamilyMembersEvent.OnAddFamilyMemberClicked -> {
                sendEffect(FamilyMembersEffect.NavigateToAddFamilyMember)
            }
            is FamilyMembersEvent.OnEditPersonalInfoClicked -> {
                sendEffect(FamilyMembersEffect.NavigateToEditPersonalInfo(event.memberId))
            }
            is FamilyMembersEvent.OnEditHealthProfileClicked -> {
                sendEffect(FamilyMembersEffect.NavigateToEditHealthProfile(event.memberId))
            }
            is FamilyMembersEvent.OnDeleteMemberClicked -> {
                sendEffect(FamilyMembersEffect.ShowDeleteConfirmation(event.memberId))
            }
            FamilyMembersEvent.OnNotificationClicked -> {
                // Notification handler
            }
        }
    }
}
