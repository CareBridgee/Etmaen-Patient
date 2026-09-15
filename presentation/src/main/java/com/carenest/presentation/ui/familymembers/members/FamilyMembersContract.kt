package com.carenest.presentation.ui.familymembers.members

data class FamilyMemberItem(
    val id: String,
    val name: String,
    val relationship: String,
    val profileImageUrl: String? = null,
    val lastCheckup: String = "",
    val upcomingService: String = ""
)

data class FamilyMembersState(
    val members: List<FamilyMemberItem> = emptyList(),
    val isLoading: Boolean = false,
    val loadFailed: Boolean = false,
    val deleteConfirmationMemberId: String? = null
)

sealed interface FamilyMembersEvent {
    data object OnBackClicked : FamilyMembersEvent
    data object OnAddFamilyMemberClicked : FamilyMembersEvent
    data class OnEditPersonalInfoClicked(val memberId: String) : FamilyMembersEvent
    data class OnEditHealthProfileClicked(val memberId: String) : FamilyMembersEvent
    data class OnDeleteMemberClicked(val memberId: String) : FamilyMembersEvent
    data object OnConfirmDeleteClicked : FamilyMembersEvent
    data object OnDismissDeleteDialogClicked : FamilyMembersEvent
    data object OnNotificationClicked : FamilyMembersEvent
    data object OnRetryClicked : FamilyMembersEvent
}

sealed interface FamilyMembersEffect {
    data object NavigateBack : FamilyMembersEffect
    data object NavigateToAddFamilyMember : FamilyMembersEffect
    data class NavigateToEditPersonalInfo(val memberId: String) : FamilyMembersEffect
    data class NavigateToEditHealthProfile(val memberId: String) : FamilyMembersEffect
    data class ShowMessage(val message: FamilyMembersMessage) : FamilyMembersEffect
}

enum class FamilyMembersMessage { Deleted, DeleteFailed, NotificationsUnavailable }
