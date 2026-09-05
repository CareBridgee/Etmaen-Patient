package com.carenest.presentation.ui.familymembers.members

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.domain.usecase.familymembers.DeleteFamilyMemberUseCase
import com.carenest.domain.usecase.familymembers.GetFamilyMembersUseCase
import com.carenest.domain.usecase.user.GetCurrentUserUseCase
import com.carenest.domain.usecase.user.ObserveCurrentUserUseCase
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FamilyMembersViewModel @Inject constructor(
    private val getFamilyMembersUseCase: GetFamilyMembersUseCase,
    private val deleteFamilyMemberUseCase: DeleteFamilyMemberUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val observeCurrentUserUseCase: ObserveCurrentUserUseCase
) : ViewModel(),
    StateHolder<FamilyMembersState> by DefaultStateHolder(FamilyMembersState()),
    EffectPublisher<FamilyMembersEffect> by DefaultEffectPublisher() {

    init {
        observeUser()
    }

    private fun observeUser() {
        viewModelScope.launch {
            observeCurrentUserUseCase().collect { user ->
                val userName = user?.name.orEmpty()
                if (userName.isBlank()) return@collect
                updateState {
                    copy(
                        members = members.map { member ->
                            val isSelf = member.id == user?.defaultProfileId ||
                                member.relationship.equals("Self", ignoreCase = true)
                            if (isSelf) {
                                member.copy(
                                    name = userName,
                                    profileImageUrl = user?.profileImageUrl ?: member.profileImageUrl,
                                )
                            } else {
                                member
                            }
                        }
                    )
                }
            }
        }
    }

    fun loadFamilyMembers() {
        if (currentState.isLoading) return
        viewModelScope.launch {
            updateState { copy(isLoading = true, loadFailed = false) }

            val cachedUser = observeCurrentUserUseCase().first()
            val currentUser = getCurrentUserUseCase().getOrNull() ?: cachedUser
            val currentUserName = currentUser?.name.orEmpty()
            val memberItems = mutableListOf<FamilyMemberItem>()

            getFamilyMembersUseCase().fold(onSuccess = { members ->
                val sortedMembers = members.sortedByDescending { member ->
                    member.isPrimary ||
                            member.id == currentUser?.defaultProfileId ||
                            member.relationship.isNullOrBlank() ||
                            member.relationship.equals("Self", ignoreCase = true) ||
                            member.relationship.equals("PRIMARY", ignoreCase = true) ||
                            member.relationship.equals("Primary", ignoreCase = true)
                }
                sortedMembers.forEach { member ->
                    if (!member.isDeleted && memberItems.none { it.id == member.id }) {
                        val isSelf = member.isPrimary ||
                                member.id == currentUser?.defaultProfileId ||
                                member.relationship.isNullOrBlank() ||
                                member.relationship.equals("Self", ignoreCase = true) ||
                                member.relationship.equals("PRIMARY", ignoreCase = true) ||
                                member.relationship.equals("Primary", ignoreCase = true)
                        val relationshipLabel = if (isSelf) "Self" else (member.relationship ?: "Member")

                        val profileName = listOfNotNull(member.firstName, member.lastName)
                            .filter { it.isNotBlank() }
                            .joinToString(" ")
                        val displayName = if (isSelf) {
                            currentUserName.ifBlank { profileName.ifBlank { "User" } }
                        } else {
                            member.fullName
                        }

                        memberItems.add(
                            FamilyMemberItem(
                                id = member.id,
                                name = displayName,
                                relationship = relationshipLabel,
                                profileImageUrl = member.profileImageUrl ?: if (isSelf) currentUser?.profileImageUrl else null,
                                lastCheckup = "",
                                upcomingService = ""
                            )
                        )
                    }
                }
            }, onFailure = {
                updateState { copy(isLoading = false, loadFailed = true) }
                return@launch
            })

            updateState { copy(members = memberItems, isLoading = false) }
        }
    }

    private fun confirmAndDeleteMember(memberId: String) {
        viewModelScope.launch {
            updateState { copy(isLoading = true, deleteConfirmationMemberId = null) }
            deleteFamilyMemberUseCase(memberId).fold(
                onSuccess = {
                    updateState { copy(isLoading = false) }
                    sendEffect(FamilyMembersEffect.ShowMessage(FamilyMembersMessage.Deleted))
                    loadFamilyMembers()
                },
                onFailure = {
                    updateState { copy(isLoading = false) }
                    sendEffect(FamilyMembersEffect.ShowMessage(FamilyMembersMessage.DeleteFailed))
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
                sendEffect(
                    FamilyMembersEffect.ShowMessage(FamilyMembersMessage.NotificationsUnavailable)
                )
            }
            FamilyMembersEvent.OnRetryClicked -> loadFamilyMembers()
        }
    }
}
