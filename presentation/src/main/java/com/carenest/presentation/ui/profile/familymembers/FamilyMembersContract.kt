package com.carenest.presentation.ui.profile.familymembers

data class FamilyMemberItem(
    val id: String,
    val name: String,
    val relationship: String,
    val avatarUrl: String? = null,
    val lastCheckup: String,
    val upcomingService: String
)

data class FamilyMembersState(
    val greeting: String = "Good morning, Elena",
    val members: List<FamilyMemberItem> = listOf(
        FamilyMemberItem(
            id = "m_1",
            name = "Maria Garcia",
            relationship = "Mother",
            lastCheckup = "Oct 12, 2023",
            upcomingService = "Dental Care"
        ),
        FamilyMemberItem(
            id = "m_2",
            name = "Roberto Garcia",
            relationship = "Father",
            lastCheckup = "Sept 28, 2023",
            upcomingService = "Blood Work"
        ),
        FamilyMemberItem(
            id = "m_3",
            name = "Sofia Garcia",
            relationship = "Daughter",
            lastCheckup = "Nov 05, 2023",
            upcomingService = "Vaccination"
        )
    ),
    val isLoading: Boolean = false
)

sealed interface FamilyMembersEvent {
    data object OnBackClicked : FamilyMembersEvent
    data object OnAddFamilyMemberClicked : FamilyMembersEvent
    data class OnEditPersonalInfoClicked(val memberId: String) : FamilyMembersEvent
    data class OnEditHealthProfileClicked(val memberId: String) : FamilyMembersEvent
    data class OnDeleteMemberClicked(val memberId: String) : FamilyMembersEvent
    data object OnNotificationClicked : FamilyMembersEvent
}

sealed interface FamilyMembersEffect {
    data object NavigateBack : FamilyMembersEffect
    data object NavigateToAddFamilyMember : FamilyMembersEffect
    data class NavigateToEditPersonalInfo(val memberId: String) : FamilyMembersEffect
    data class NavigateToEditHealthProfile(val memberId: String) : FamilyMembersEffect
    data class ShowDeleteConfirmation(val memberId: String) : FamilyMembersEffect
}
