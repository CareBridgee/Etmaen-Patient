package com.carenest.presentation.ui.family_members.add

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.domain.model.family_members.FamilyRelationship
import com.carenest.domain.usecase.family_members.CreateFamilyMemberUseCase
import com.carenest.domain.usecase.family_members.GetFamilyMemberByIdUseCase
import com.carenest.domain.usecase.family_members.UpdateFamilyMemberUseCase
import com.carenest.domain.validation.PhoneValidator
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddFamilyMemberViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val createFamilyMemberUseCase: CreateFamilyMemberUseCase,
    private val updateFamilyMemberUseCase: UpdateFamilyMemberUseCase,
    private val getFamilyMemberByIdUseCase: GetFamilyMemberByIdUseCase
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
                onSuccess = { familyMember ->
                    val relEnum = FamilyRelationship.fromBackend(familyMember.relationship)
                    updateState {
                        copy(
                            contactName = familyMember.contactName,
                            relationship = relEnum,
                            phoneNumber = familyMember.phoneNumber,
                            isLoadingData = false
                        )
                    }
                },
                onFailure = {
                    updateState { copy(isLoadingData = false) }
                }
            )
        }
    }

    fun onEvent(event: AddFamilyMemberEvent) {
        when (event) {
            is AddFamilyMemberEvent.RelationshipSelected -> {
                updateState { copy(relationship = event.relationship, relationshipError = null) }
            }
            is AddFamilyMemberEvent.ContactNameChanged -> {
                val nameError = if (event.value.trim().isBlank()) "Contact name is required" else null
                updateState { copy(contactName = event.value, contactNameError = nameError) }
            }
            is AddFamilyMemberEvent.PhoneNumberChanged -> {
                val cleaned = PhoneValidator.clean(event.value)
                val phoneError = when {
                    cleaned.isBlank() -> "Phone number is required"
                    !PhoneValidator.isValid(cleaned) -> "Please enter a valid international phone number e.g. +1234567890"
                    else -> null
                }
                updateState { copy(phoneNumber = cleaned, phoneNumberError = phoneError) }
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
        var hasError = false

        if (currentState.relationship == null) {
            updateState { copy(relationshipError = "Please select a relationship") }
            hasError = true
        }
        if (currentState.contactName.trim().isBlank()) {
            updateState { copy(contactNameError = "Contact name is required") }
            hasError = true
        }
        val cleanedPhone = PhoneValidator.clean(currentState.phoneNumber)
        if (cleanedPhone.isBlank()) {
            updateState { copy(phoneNumberError = "Phone number is required") }
            hasError = true
        } else if (!PhoneValidator.isValid(cleanedPhone)) {
            updateState { copy(phoneNumberError = "Please enter a valid international phone number e.g. +1234567890") }
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            updateState { copy(isSubmitting = true) }
            val relStr = currentState.relationship?.backendValue ?: "Other"
            val currentId = currentState.memberId

            val result = if (currentState.isEditMode && !currentId.isNullOrBlank()) {
                updateFamilyMemberUseCase(
                    id = currentId,
                    relationship = relStr,
                    contactName = currentState.contactName.trim(),
                    phoneNumber = cleanedPhone
                )
            } else {
                createFamilyMemberUseCase(
                    relationship = relStr,
                    contactName = currentState.contactName.trim(),
                    phoneNumber = cleanedPhone
                )
            }

            result.fold(
                onSuccess = {
                    updateState { copy(isSubmitting = false) }
                    sendEffect(AddFamilyMemberEffect.ShowSuccess)
                    sendEffect(AddFamilyMemberEffect.NavigateBack)
                },
                onFailure = { error ->
                    updateState { copy(isSubmitting = false) }
                    val msg = error.message ?: "Failed to save family member"
                    updateState { copy(errorMessage = msg) }
                    sendEffect(AddFamilyMemberEffect.ShowError(msg))
                }
            )
        }
    }
}
