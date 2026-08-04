package com.carenest.presentation.ui.family_members.members

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.domain.usecase.family_members.DeleteFamilyMemberUseCase
import com.carenest.domain.usecase.family_members.GetFamilyMembersUseCase
import com.carenest.domain.usecase.profile.GetDefaultProfileUseCase
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FamilyMembersViewModel @Inject constructor(
    private val getDefaultProfileUseCase: GetDefaultProfileUseCase,
    private val getFamilyMembersUseCase: GetFamilyMembersUseCase,
    private val deleteFamilyMemberUseCase: DeleteFamilyMemberUseCase
) : ViewModel(),
    StateHolder<FamilyMembersState> by DefaultStateHolder(FamilyMembersState()),
    EffectPublisher<FamilyMembersEffect> by DefaultEffectPublisher() {

    init {
        loadFamilyMembers()
    }

    fun loadFamilyMembers() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }

            val memberItems = mutableListOf<FamilyMemberItem>()

            // Load Self profile first
            getDefaultProfileUseCase().onSuccess { defaultProfile ->
                val fullName = listOfNotNull(defaultProfile.firstName, defaultProfile.lastName)
                    .joinToString(" ")
                    .ifBlank { "Self" }
                memberItems.add(
                    FamilyMemberItem(
                        id = defaultProfile.id,
                        name = fullName,
                        relationship = "Self",
                        lastCheckup = "N/A",
                        upcomingService = "Checkup"
                    )
                )
            }

            // Load family members
            getFamilyMembersUseCase().onSuccess { members ->
                members.forEach { member ->
                    if (memberItems.none { it.id == member.id }) {
                        val displayName = member.contactName.ifBlank { member.relationship ?: "Family Member" }
                        val relationshipLabel = member.relationship ?: "Member"
                        memberItems.add(
                            FamilyMemberItem(
                                id = member.id,
                                name = displayName,
                                relationship = relationshipLabel,
                                lastCheckup = "N/A",
                                upcomingService = "Checkup"
                            )
                        )
                    }
                }
            }

            updateState { copy(members = memberItems, isLoading = false) }
        }
    }

    private fun confirmAndDeleteMember(memberId: String) {
        viewModelScope.launch {
            updateState { copy(isLoading = true, deleteConfirmationMemberId = null) }
            deleteFamilyMemberUseCase(memberId).fold(
                onSuccess = {
                    sendEffect(FamilyMembersEffect.ShowToast("Family member deleted successfully"))
                    loadFamilyMembers()
                },
                onFailure = { error ->
                    updateState { copy(isLoading = false) }
                    val msg = error.message ?: "Failed to delete family member"
                    sendEffect(FamilyMembersEffect.ShowToast(msg))
                }
            )
        }
    }

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
                updateState { copy(deleteConfirmationMemberId = event.memberId) }
            }
            FamilyMembersEvent.OnConfirmDeleteClicked -> {
                val memberId = state.value.deleteConfirmationMemberId
                if (memberId != null) {
                    confirmAndDeleteMember(memberId)
                }
            }
            FamilyMembersEvent.OnDismissDeleteDialogClicked -> {
                updateState { copy(deleteConfirmationMemberId = null) }
            }
            FamilyMembersEvent.OnNotificationClicked -> {
                // Notification handler
            }
        }
    }
}
