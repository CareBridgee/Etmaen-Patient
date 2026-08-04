package com.carenest.presentation.ui.family_members.add

import com.carenest.domain.model.family_members.FamilyRelationship

data class AddFamilyMemberState(
    val memberId: String? = null,
    val isEditMode: Boolean = false,
    val relationship: FamilyRelationship? = null,
    val contactName: String = "",
    val phoneNumber: String = "",
    val isSubmitting: Boolean = false,
    val isLoadingData: Boolean = false,
    val relationshipError: String? = null,
    val contactNameError: String? = null,
    val phoneNumberError: String? = null,
    val errorMessage: String? = null
)

sealed interface AddFamilyMemberEvent {
    data class RelationshipSelected(val relationship: FamilyRelationship) : AddFamilyMemberEvent
    data class ContactNameChanged(val value: String) : AddFamilyMemberEvent
    data class PhoneNumberChanged(val value: String) : AddFamilyMemberEvent
    data object BackClicked : AddFamilyMemberEvent
    data object SaveClicked : AddFamilyMemberEvent
}

sealed interface AddFamilyMemberEffect {
    data object NavigateBack : AddFamilyMemberEffect
    data object ShowSuccess : AddFamilyMemberEffect
    data class ShowError(val message: String) : AddFamilyMemberEffect
}
