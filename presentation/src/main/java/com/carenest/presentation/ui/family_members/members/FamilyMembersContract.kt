package com.carenest.presentation.ui.family_members.members

data class FamilyMemberItem(
    val id: String,
    val name: String,
    val relationship: String,
    val lastCheckup: String = "N/A",
    val upcomingService: String = "Checkup"
)

data class FamilyMembersState(
    val members: List<FamilyMemberItem> = emptyList(),
    val isLoading: Boolean = false,
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
}

sealed interface FamilyMembersEffect {
    data object NavigateBack : FamilyMembersEffect
    data object NavigateToAddFamilyMember : FamilyMembersEffect
    data class NavigateToEditPersonalInfo(val memberId: String) : FamilyMembersEffect
    data class NavigateToEditHealthProfile(val memberId: String) : FamilyMembersEffect
    data class ShowToast(val message: String) : FamilyMembersEffect
}
