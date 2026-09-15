package com.carenest.presentation.ui.familymembers.add

import com.carenest.domain.model.familymembers.FamilyRelationship
import com.carenest.domain.validation.PhoneNumberValidationError

data class AddFamilyMemberState(
    val memberId: String? = null,
    val isEditMode: Boolean = false,
    val relationship: FamilyRelationship? = null,
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val dateOfBirth: String = "",
    val gender: String = "MALE",
    val avatarUri: String? = null,
    val profileImageUrl: String? = null,
    val selectedAvatarBytes: ByteArray? = null,
    val selectedAvatarFileName: String? = null,
    val selectedAvatarContentType: String? = null,
    val bloodType: String = "",
    val height: String = "",
    val weight: String = "",
    val mobilityStatus: String = "",
    val mobilityNotes: String = "",
    val previousSurgeries: String = "",
    val previousHospitalizations: String = "",
    val isSubmitting: Boolean = false,
    val isLoadingData: Boolean = false,
    val relationshipError: String? = null,
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val phoneNumberError: PhoneNumberValidationError? = null,
    val dateOfBirthError: String? = null,
    val genderError: String? = null,
    val heightError: String? = null,
    val weightError: String? = null,
    val errorMessage: String? = null
)

sealed interface AddFamilyMemberEvent {
    data class RelationshipSelected(val relationship: FamilyRelationship) : AddFamilyMemberEvent
    data class FirstNameChanged(val value: String) : AddFamilyMemberEvent
    data class LastNameChanged(val value: String) : AddFamilyMemberEvent
    data class PhoneNumberChanged(val value: String) : AddFamilyMemberEvent
    data class DateOfBirthChanged(val value: String) : AddFamilyMemberEvent
    data class GenderSelected(val gender: String) : AddFamilyMemberEvent
    data class AvatarSelected(
        val uri: String,
        val fileName: String,
        val contentType: String,
        val bytes: ByteArray
    ) : AddFamilyMemberEvent
    data object EditAvatarClicked : AddFamilyMemberEvent
    data class BloodTypeChanged(val value: String) : AddFamilyMemberEvent
    data class HeightChanged(val value: String) : AddFamilyMemberEvent
    data class WeightChanged(val value: String) : AddFamilyMemberEvent
    data class MobilityStatusChanged(val value: String) : AddFamilyMemberEvent
    data class MobilityNotesChanged(val value: String) : AddFamilyMemberEvent
    data class PreviousSurgeriesChanged(val value: String) : AddFamilyMemberEvent
    data class PreviousHospitalizationsChanged(val value: String) : AddFamilyMemberEvent
    data object BackClicked : AddFamilyMemberEvent
    data object SaveClicked : AddFamilyMemberEvent
}

sealed interface AddFamilyMemberEffect {
    data object NavigateBack : AddFamilyMemberEffect
    data object ShowSuccess : AddFamilyMemberEffect
    data class NavigateToCompleteProfile(val memberId: String) : AddFamilyMemberEffect
    data class ShowError(val message: String) : AddFamilyMemberEffect
    data object SelectAvatar : AddFamilyMemberEffect
}
