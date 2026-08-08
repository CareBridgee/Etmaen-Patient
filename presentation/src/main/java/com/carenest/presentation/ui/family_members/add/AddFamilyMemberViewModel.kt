package com.carenest.presentation.ui.family_members.add

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.domain.model.family_members.FamilyMemberInput
import com.carenest.domain.model.family_members.FamilyRelationship
import com.carenest.domain.usecase.family_members.CreateFamilyMemberUseCase
import com.carenest.domain.usecase.family_members.GetFamilyMemberByIdUseCase
import com.carenest.domain.usecase.family_members.UpdateFamilyMemberUseCase
import com.carenest.domain.validation.EgyptianPhoneNumberValidator
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import com.carenest.domain.repository.UserRepository
import com.carenest.domain.model.profile.Gender
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddFamilyMemberViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val createFamilyMemberUseCase: CreateFamilyMemberUseCase,
    private val updateFamilyMemberUseCase: UpdateFamilyMemberUseCase,
    private val getFamilyMemberByIdUseCase: GetFamilyMemberByIdUseCase,
    private val userRepository: UserRepository
) : ViewModel(),
    StateHolder<AddFamilyMemberState> by DefaultStateHolder(AddFamilyMemberState()),
    EffectPublisher<AddFamilyMemberEffect> by DefaultEffectPublisher() {

    private val memberId: String? = savedStateHandle["memberId"]

    init {
        val id = memberId
        if (!id.isNullOrBlank() && id != "null") {
            initMemberId(id)
        }
    }

    fun initMemberId(id: String) {
        if (state.value.memberId != id || !state.value.isEditMode) {
            updateState { copy(memberId = id, isEditMode = true) }
            loadMemberDetails(id)
        }
    }

    private fun loadMemberDetails(id: String) {
        viewModelScope.launch {
            updateState { copy(isLoadingData = true) }
            getFamilyMemberByIdUseCase(id).fold(
                onSuccess = { member ->
                    val relEnum = FamilyRelationship.fromBackend(member.relationship)
                    updateState {
                        copy(
                            firstName = member.firstName.orEmpty(),
                            lastName = member.lastName.orEmpty(),
                            phoneNumber = EgyptianPhoneNumberValidator.sanitizeInput(member.phoneNumber.orEmpty()),
                            relationship = relEnum,
                            dateOfBirth = member.dateOfBirth.orEmpty(),
                            gender = Gender.fromApi(member.gender).apiValue,
                            profileImageUrl = member.profileImageUrl,
                            bloodType = member.bloodType.orEmpty(),
                            height = member.height?.toString().orEmpty(),
                            weight = member.weight?.toString().orEmpty(),
                            mobilityStatus = member.mobilityStatus.orEmpty(),
                            mobilityNotes = member.mobilityNotes.orEmpty(),
                            previousSurgeries = member.previousSurgeries.orEmpty(),
                            previousHospitalizations = member.previousHospitalizations.orEmpty(),
                            isLoadingData = false
                        )
                    }
                },
                onFailure = {
                    updateState { copy(isLoadingData = false) }
                    sendEffect(AddFamilyMemberEffect.ShowError("family_member_load_failed"))
                }
            )
        }
    }

    fun onEvent(event: AddFamilyMemberEvent) {
        when (event) {
            is AddFamilyMemberEvent.RelationshipSelected -> {
                updateState { copy(relationship = event.relationship, relationshipError = null) }
            }
            is AddFamilyMemberEvent.FirstNameChanged -> {
                val err = if (event.value.trim().isBlank()) "First name is required" else null
                updateState { copy(firstName = event.value, firstNameError = err) }
            }
            is AddFamilyMemberEvent.LastNameChanged -> {
                val err = if (event.value.trim().isBlank()) "Last name is required" else null
                updateState { copy(lastName = event.value, lastNameError = err) }
            }
            is AddFamilyMemberEvent.PhoneNumberChanged -> {
                val phoneNumber = EgyptianPhoneNumberValidator.sanitizeInput(event.value)
                updateState {
                    copy(
                        phoneNumber = phoneNumber,
                        phoneNumberError = EgyptianPhoneNumberValidator.validate(phoneNumber)
                    )
                }
            }
            is AddFamilyMemberEvent.DateOfBirthChanged -> {
                val err = if (event.value.trim().isBlank()) "Date of birth is required" else null
                updateState { copy(dateOfBirth = event.value, dateOfBirthError = err) }
            }
            is AddFamilyMemberEvent.GenderSelected -> {
                updateState { copy(gender = event.gender, genderError = null) }
            }
            is AddFamilyMemberEvent.AvatarSelected -> updateState {
                copy(
                    avatarUri = event.uri,
                    selectedAvatarFileName = event.fileName,
                    selectedAvatarContentType = event.contentType,
                    selectedAvatarBytes = event.bytes
                )
            }
            AddFamilyMemberEvent.EditAvatarClicked -> sendEffect(AddFamilyMemberEffect.SelectAvatar)
            is AddFamilyMemberEvent.BloodTypeChanged -> {
                updateState { copy(bloodType = event.value) }
            }
            is AddFamilyMemberEvent.HeightChanged -> {
                val err = if (event.value.isNotBlank() && event.value.toDoubleOrNull() == null) "Must be a number" else null
                updateState { copy(height = event.value, heightError = err) }
            }
            is AddFamilyMemberEvent.WeightChanged -> {
                val err = if (event.value.isNotBlank() && event.value.toDoubleOrNull() == null) "Must be a number" else null
                updateState { copy(weight = event.value, weightError = err) }
            }
            is AddFamilyMemberEvent.MobilityStatusChanged -> {
                updateState { copy(mobilityStatus = event.value) }
            }
            is AddFamilyMemberEvent.MobilityNotesChanged -> {
                updateState { copy(mobilityNotes = event.value) }
            }
            is AddFamilyMemberEvent.PreviousSurgeriesChanged -> {
                updateState { copy(previousSurgeries = event.value) }
            }
            is AddFamilyMemberEvent.PreviousHospitalizationsChanged -> {
                updateState { copy(previousHospitalizations = event.value) }
            }
            AddFamilyMemberEvent.BackClicked -> {
                sendEffect(AddFamilyMemberEffect.NavigateBack)
            }
            AddFamilyMemberEvent.SaveClicked -> {
                submitForm()
            }
        }
    }

    private fun submitForm() {
        val currentState = state.value
        if (currentState.isSubmitting || currentState.isLoadingData) return
        var hasError = false

        if (currentState.relationship == null) {
            updateState { copy(relationshipError = "Please select a relationship") }
            hasError = true
        }
        if (currentState.firstName.trim().isBlank()) {
            updateState { copy(firstNameError = "First name is required") }
            hasError = true
        }
        if (currentState.lastName.trim().isBlank()) {
            updateState { copy(lastNameError = "Last name is required") }
            hasError = true
        }
        if (currentState.phoneNumber.isNotBlank()) {
            EgyptianPhoneNumberValidator.validate(currentState.phoneNumber)?.let { error ->
                updateState { copy(phoneNumberError = error) }
                hasError = true
            }
        }
        if (currentState.dateOfBirth.trim().isBlank()) {
            updateState { copy(dateOfBirthError = "Date of birth is required") }
            hasError = true
        }

        val heightVal = currentState.height.toDoubleOrNull()
        if (currentState.height.isNotBlank() && heightVal == null) {
            updateState { copy(heightError = "Height must be a number") }
            hasError = true
        }

        val weightVal = currentState.weight.toDoubleOrNull()
        if (currentState.weight.isNotBlank() && weightVal == null) {
            updateState { copy(weightError = "Weight must be a number") }
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            updateState { copy(isSubmitting = true) }
            val relStr = currentState.relationship?.backendValue ?: "Other"

            val finalAvatarUrl = if (currentState.selectedAvatarBytes != null && currentState.selectedAvatarBytes.isNotEmpty()) {
                val uploadResult = userRepository.uploadProfileImage(
                    fileName = currentState.selectedAvatarFileName ?: "profile.jpg",
                    contentType = currentState.selectedAvatarContentType ?: "image/jpeg",
                    bytes = currentState.selectedAvatarBytes
                )
                uploadResult.getOrElse {
                    updateState { copy(isSubmitting = false) }
                    sendEffect(AddFamilyMemberEffect.ShowError("Unable to upload profile photo"))
                    return@launch
                }
            } else {
                currentState.profileImageUrl
            }

            val input = FamilyMemberInput(
                relationship = relStr,
                firstName = currentState.firstName.trim(),
                lastName = currentState.lastName.trim(),
                phoneNumber = currentState.phoneNumber,
                dateOfBirth = currentState.dateOfBirth.trim(),
                gender = currentState.gender,
                bloodType = currentState.bloodType.ifBlank { null },
                height = heightVal,
                weight = weightVal,
                mobilityStatus = currentState.mobilityStatus.ifBlank { null },
                mobilityNotes = currentState.mobilityNotes.ifBlank { null },
                previousSurgeries = currentState.previousSurgeries.ifBlank { null },
                previousHospitalizations = currentState.previousHospitalizations.ifBlank { null },
                profileImageUrl = finalAvatarUrl
            )

            val currentId = currentState.memberId
            val result = if (currentState.isEditMode && !currentId.isNullOrBlank()) {
                updateFamilyMemberUseCase(currentId, input)
            } else {
                createFamilyMemberUseCase(input)
            }

            result.fold(
                onSuccess = {
                    updateState { copy(isSubmitting = false) }
                    sendEffect(AddFamilyMemberEffect.ShowSuccess)
                    sendEffect(AddFamilyMemberEffect.NavigateBack)
                },
                onFailure = {
                    updateState { copy(isSubmitting = false) }
                    updateState { copy(errorMessage = "family_member_save_failed") }
                    sendEffect(AddFamilyMemberEffect.ShowError("family_member_save_failed"))
                }
            )
        }
    }
}
