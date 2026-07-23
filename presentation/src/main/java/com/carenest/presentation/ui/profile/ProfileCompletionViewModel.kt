package com.carenest.presentation.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.domain.model.profile.*
import com.carenest.domain.config.TemporaryCompleteProfileTestConfig
import com.carenest.domain.usecase.profile.*
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import com.carenest.presentation.ui.profile.validation.ProfileField
import com.carenest.presentation.ui.profile.validation.ProfileInputValidator
import com.carenest.presentation.ui.profile.validation.ProfileValidationError
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileCompletionViewModel @Inject constructor(
    private val getDefaultProfile: GetDefaultProfileUseCase,
    private val updateBasicHealth: UpdateBasicHealthUseCase,
    private val updateMedicalHistory: UpdateMedicalHistoryUseCase,
    private val loadMedicalConditions: LoadMedicalConditionsUseCase,
    private val syncMedicalConditions: SyncMedicalConditionsUseCase,
    private val loadAllergies: LoadAllergiesUseCase,
    private val syncAllergies: SyncAllergiesUseCase,
    private val loadEmergencyContacts: LoadEmergencyContactsUseCase,
    private val saveEmergencyContact: SaveEmergencyContactUseCase,
    private val loadProfileDraft: LoadProfileDraftUseCase,
    private val saveProfileDraft: SaveProfileDraftUseCase,
    private val markOnboardingHandled: MarkHealthProfileOnboardingHandledUseCase
) : ViewModel(),
    StateHolder<ProfileCompletionState> by DefaultStateHolder(ProfileCompletionState()),
    EffectPublisher<ProfileCompletionEffect> by DefaultEffectPublisher() {

    init {
        initialize()
    }

    fun onEvent(event: ProfileCompletionIntent) {
        when (event) {
            is ProfileCompletionIntent.HeightChanged -> edit(ProfileField.Height) {
                copy(height = event.height.filter(Char::isDigit).take(3))
            }
            is ProfileCompletionIntent.WeightChanged -> edit(ProfileField.Weight) {
                copy(weight = event.weight.filter(Char::isDigit).take(3))
            }
            is ProfileCompletionIntent.BloodTypeChanged -> edit(ProfileField.BloodType) {
                copy(bloodType = event.bloodType.replace('−', '-'))
            }
            is ProfileCompletionIntent.ConditionToggled -> edit {
                copy(selectedConditionKeys = selectedConditionKeys.toggle(event.localKey))
            }
            is ProfileCompletionIntent.OtherConditionsChanged -> edit(ProfileField.OtherConditions) {
                copy(otherConditions = event.conditions.take(500))
            }
            ProfileCompletionIntent.NoKnownAllergiesToggled -> edit(ProfileField.AllergiesSelection) {
                val none = !hasNoKnownAllergies
                copy(
                    hasNoKnownAllergies = none,
                    selectedAllergyKeys = if (none) emptySet() else selectedAllergyKeys,
                    otherAllergies = if (none) "" else otherAllergies,
                    validationErrors = validationErrors - ProfileField.OtherAllergies
                )
            }
            is ProfileCompletionIntent.AllergyToggled -> edit(ProfileField.AllergiesSelection) {
                copy(
                    hasNoKnownAllergies = false,
                    selectedAllergyKeys = selectedAllergyKeys.toggle(event.localKey)
                )
            }
            is ProfileCompletionIntent.OtherAllergiesChanged -> edit(
                ProfileField.OtherAllergies,
                ProfileField.AllergiesSelection
            ) {
                copy(hasNoKnownAllergies = false, otherAllergies = event.allergies.take(500))
            }
            ProfileCompletionIntent.NoCurrentMedicationsToggled -> edit(ProfileField.MedicationsSelection) {
                val none = !hasNoCurrentMedications
                copy(
                    hasNoCurrentMedications = none,
                    currentMedications = if (none) emptyList() else listOf(blankMedication()),
                    medicationValidationErrors = emptyMap()
                )
            }
            ProfileCompletionIntent.MedicationAdded -> edit(ProfileField.MedicationsSelection) {
                if (hasNoCurrentMedications || currentMedications.size >= MAX_MEDICATIONS) this else copy(
                    currentMedications = currentMedications + blankMedication()
                )
            }
            is ProfileCompletionIntent.MedicationNameChanged -> updateMedication(event.index) {
                copy(name = event.value.take(100))
            }
            is ProfileCompletionIntent.MedicationRemoved -> edit(ProfileField.MedicationsSelection) {
                if (event.index !in currentMedications.indices) this else {
                    val removedId = currentMedications[event.index].localId
                    copy(
                        currentMedications = currentMedications.filterIndexed { index, _ -> index != event.index },
                        medicationValidationErrors = medicationValidationErrors - removedId
                    )
                }
            }
            is ProfileCompletionIntent.PreviousSurgeriesChanged -> edit(ProfileField.PreviousSurgeries) {
                copy(previousSurgeries = event.surgeries.take(1000))
            }
            is ProfileCompletionIntent.PreviousHospitalizationsChanged -> edit(ProfileField.PreviousHospitalizations) {
                copy(previousHospitalizations = event.hospitalizations.take(1000))
            }
            is ProfileCompletionIntent.MobilityStatusSelected -> edit(ProfileField.MobilityStatus) {
                copy(mobilityStatus = event.status)
            }
            is ProfileCompletionIntent.MobilityNotesChanged -> edit(ProfileField.MobilityNotes) {
                copy(mobilityNotes = event.notes.take(500))
            }
            is ProfileCompletionIntent.EmergencyContactNameChanged -> edit(ProfileField.EmergencyContactName) {
                copy(emergencyContactName = event.name.take(100))
            }
            is ProfileCompletionIntent.EmergencyRelationshipSelected -> edit(ProfileField.EmergencyRelationship) {
                copy(emergencyRelationship = event.relationship)
            }
            is ProfileCompletionIntent.EmergencyPhoneNumberChanged -> edit(ProfileField.EmergencyPhoneNumber) {
                copy(emergencyPhoneNumber = event.phoneNumber.filter { it.isDigit() || it in "+ -" }.take(20))
            }
            ProfileCompletionIntent.BackClicked -> navigateBack()
            ProfileCompletionIntent.ContinueClicked -> continueFlow()
            ProfileCompletionIntent.SkipClicked -> finishHealthOnboarding()
            ProfileCompletionIntent.RetryClicked -> retry()
        }
    }

    private fun initialize() {
        if (currentState.initialized) return
        updateState { copy(initialized = true, isInitializing = true, errorMessage = null) }
        viewModelScope.launch {
            val profile = if (TemporaryCompleteProfileTestConfig.ENABLED) {
                temporaryDebugProfile(TemporaryCompleteProfileTestConfig.PROFILE_ID)
            } else {
                getDefaultProfile().getOrElse {
                    updateState { copy(isInitializing = false, errorMessage = it.userMessage()) }
                    return@launch
                }
            }
            val userKey = profile.userId?.takeIf(String::isNotBlank) ?: profile.id
            val draft = loadProfileDraft(userKey).getOrElse {
                updateState { copy(isInitializing = false, errorMessage = it.userMessage()) }
                return@launch
            }
            val contactsResult = loadEmergencyContacts(profile.id)
            val contacts = contactsResult.getOrDefault(emptyList())
            updateState {
                hydrateProfile(profile)
                    .copy(
                        userKey = userKey,
                        localDraft = draft,
                        otherConditions = draft.otherConditions,
                        otherAllergies = draft.otherAllergies,
                        hasNoKnownAllergies = draft.noKnownAllergiesConfirmed,
                        currentMedications = if (
                            draft.noCurrentMedicationsConfirmed || draft.medications.isNotEmpty()
                        ) draft.medications else listOf(blankMedication()),
                        hasNoCurrentMedications = draft.noCurrentMedicationsConfirmed,
                        mobilityStatus = draft.pendingMobilityStatus?.toMobilityStatus(),
                        mobilityNotes = draft.pendingMobilityNotes.orEmpty()
                    )
                    .hydrateContacts(contacts)
                    .copy(
                        isInitializing = false,
                        emergencyContactsLoaded = contactsResult.isSuccess,
                        loadedSteps = if (contactsResult.isSuccess) {
                            loadedSteps + ProfileStep.EmergencyContact
                        } else loadedSteps
                    )
            }
        }
    }

    private fun continueFlow() {
        if (currentState.isInitializing || currentState.isLoadingStep || currentState.isSubmitting) return
        when (currentState.currentStep) {
            ProfileStep.Welcome -> moveTo(ProfileStep.BasicHealthInfo)
            ProfileStep.BasicHealthInfo -> submitBasicHealth()
            ProfileStep.MedicalConditions -> submitMedicalConditions()
            ProfileStep.Allergies -> submitAllergies()
            ProfileStep.CurrentMedications -> submitMedications()
            ProfileStep.MedicalHistory -> submitMedicalHistory()
            ProfileStep.MobilityStatus -> submitMobility()
            ProfileStep.EmergencyContact, ProfileStep.FinalStep -> submitEmergencyContact()
        }
    }

    private fun submitBasicHealth() {
        val snapshot = currentState
        val errors = ProfileInputValidator.basicHealth(snapshot.height, snapshot.weight, snapshot.bloodType)
        if (errors.isNotEmpty()) return showValidationErrors(BASIC_HEALTH_FIELDS, errors)

        val height = snapshot.height.toDouble()
        val weight = snapshot.weight.toDouble()
        val bloodType = snapshot.bloodType.replace('−', '-').trim()
        val profileId = requireProfileId() ?: return
        launchSubmission {
            updateBasicHealth(profileId, BasicHealthUpdate(height, weight, bloodType)).fold(
                onSuccess = { profile ->
                    updateState {
                        hydrateProfile(profile).copy(
                            isSubmitting = false,
                            validationErrors = validationErrors - BASIC_HEALTH_FIELDS
                        )
                    }
                    moveTo(ProfileStep.MedicalConditions)
                },
                onFailure = ::finishFailure
            )
        }
    }

    private fun submitMedicalConditions() {
        val snapshot = currentState
        val errors = ProfileInputValidator.medicalConditions(snapshot.otherConditions)
        if (errors.isNotEmpty()) return showValidationErrors(MEDICAL_CONDITION_FIELDS, errors)

        val profileId = requireProfileId() ?: return
        val userKey = requireUserKey() ?: return
        val draft = snapshot.localDraft.copy(otherConditions = snapshot.otherConditions.trim())
        launchSubmission {
            val syncedIds = syncMedicalConditions(
                profileId,
                snapshot.originalConditionBackendIds,
                snapshot.selectedConditionKeys
            ).getOrElse {
                finishFailure(it)
                return@launchSubmission
            }
            saveProfileDraft(userKey, draft).getOrElse {
                finishFailure(it)
                return@launchSubmission
            }
            updateState {
                copy(
                    isSubmitting = false,
                    localDraft = draft,
                    otherConditions = draft.otherConditions,
                    validationErrors = validationErrors - MEDICAL_CONDITION_FIELDS,
                    originalConditionBackendIds = syncedIds
                )
            }
            moveTo(ProfileStep.Allergies)
        }
    }

    private fun submitAllergies() {
        val snapshot = currentState
        val errors = ProfileInputValidator.allergies(
            snapshot.hasNoKnownAllergies,
            snapshot.selectedAllergyKeys,
            snapshot.otherAllergies
        )
        if (errors.isNotEmpty()) return showValidationErrors(ALLERGY_FIELDS, errors)

        val profileId = requireProfileId() ?: return
        val userKey = requireUserKey() ?: return
        val selectedKeys = if (snapshot.hasNoKnownAllergies) emptySet() else snapshot.selectedAllergyKeys
        val draft = snapshot.localDraft.copy(
            otherAllergies = if (snapshot.hasNoKnownAllergies) "" else snapshot.otherAllergies.trim(),
            noKnownAllergiesConfirmed = snapshot.hasNoKnownAllergies
        )
        launchSubmission {
            val syncedIds = syncAllergies(
                profileId,
                snapshot.originalAllergyBackendIds,
                selectedKeys
            ).getOrElse {
                finishFailure(it)
                return@launchSubmission
            }
            saveProfileDraft(userKey, draft).getOrElse {
                finishFailure(it)
                return@launchSubmission
            }
            updateState {
                copy(
                    isSubmitting = false,
                    localDraft = draft,
                    selectedAllergyKeys = selectedKeys,
                    otherAllergies = draft.otherAllergies,
                    validationErrors = validationErrors - ALLERGY_FIELDS,
                    originalAllergyBackendIds = syncedIds
                )
            }
            moveTo(ProfileStep.CurrentMedications)
        }
    }

    private fun submitMedications() {
        val snapshot = currentState
        val validation = ProfileInputValidator.medications(
            snapshot.hasNoCurrentMedications,
            snapshot.currentMedications
        )
        if (!validation.isValid) {
            updateState {
                copy(
                    validationErrors = (validationErrors - MEDICATION_FIELDS) + validation.fieldErrors,
                    medicationValidationErrors = validation.entryErrors,
                    errorMessage = null
                )
            }
            return
        }

        val userKey = requireUserKey() ?: return
        val entries = if (snapshot.hasNoCurrentMedications) {
            emptyList()
        } else {
            snapshot.currentMedications
                .filter { it.name.isNotBlank() }
                .map { it.copy(name = it.name.trim()) }
        }
        val draft = snapshot.localDraft.copy(
            medications = entries,
            noCurrentMedicationsConfirmed = snapshot.hasNoCurrentMedications
        )
        launchSubmission {
            saveProfileDraft(userKey, draft).getOrElse {
                finishFailure(it)
                return@launchSubmission
            }
            updateState {
                copy(
                    isSubmitting = false,
                    localDraft = draft,
                    currentMedications = entries,
                    validationErrors = validationErrors - MEDICATION_FIELDS,
                    medicationValidationErrors = emptyMap()
                )
            }
            moveTo(ProfileStep.MedicalHistory)
        }
    }

    private fun submitMedicalHistory() {
        val snapshot = currentState
        val errors = ProfileInputValidator.medicalHistory(
            snapshot.previousSurgeries,
            snapshot.previousHospitalizations
        )
        if (errors.isNotEmpty()) return showValidationErrors(MEDICAL_HISTORY_FIELDS, errors)

        val profileId = requireProfileId() ?: return
        val surgeries = snapshot.previousSurgeries.trim()
        val hospitalizations = snapshot.previousHospitalizations.trim()
        launchSubmission {
            updateMedicalHistory(
                profileId,
                MedicalHistoryUpdate(surgeries, hospitalizations)
            ).fold(
                onSuccess = { profile ->
                    updateState {
                        hydrateProfile(profile).copy(
                            isSubmitting = false,
                            validationErrors = validationErrors - MEDICAL_HISTORY_FIELDS
                        )
                    }
                    moveTo(ProfileStep.MobilityStatus)
                },
                onFailure = ::finishFailure
            )
        }
    }

    private fun submitMobility() {
        val snapshot = currentState
        val errors = ProfileInputValidator.mobility(snapshot.mobilityStatus, snapshot.mobilityNotes)
        if (errors.isNotEmpty()) return showValidationErrors(MOBILITY_FIELDS, errors)

        val status = requireNotNull(snapshot.mobilityStatus)
        val userKey = requireUserKey() ?: return
        val draft = snapshot.localDraft.copy(
            pendingMobilityStatus = status.wireValue(),
            pendingMobilityNotes = snapshot.mobilityNotes.trim()
        )
        launchSubmission {
            saveProfileDraft(userKey, draft).getOrElse {
                finishFailure(it)
                return@launchSubmission
            }
            updateState {
                copy(
                    isSubmitting = false,
                    localDraft = draft,
                    mobilityStatus = status,
                    mobilityNotes = draft.pendingMobilityNotes.orEmpty(),
                    validationErrors = validationErrors - MOBILITY_FIELDS
                )
            }
            moveTo(ProfileStep.EmergencyContact)
        }
    }

    private fun submitEmergencyContact() {
        if (!currentState.emergencyContactsLoaded) {
            ensureStepLoaded(ProfileStep.EmergencyContact)
            return
        }
        if (currentState.emergencyContacts.size > 1 && currentState.emergencyContactId == null) {
            return finishHealthOnboarding()
        }

        val snapshot = currentState
        val errors = ProfileInputValidator.emergencyContact(
            snapshot.emergencyContactName,
            snapshot.emergencyRelationship,
            snapshot.emergencyPhoneNumber
        )
        if (errors.isNotEmpty()) return showValidationErrors(EMERGENCY_FIELDS, errors)

        val name = snapshot.emergencyContactName.trim()
        val phone = snapshot.emergencyPhoneNumber.trim()
        val relationship = requireNotNull(snapshot.emergencyRelationship).wireValue()
        val profileId = requireProfileId() ?: return
        val contactId = snapshot.emergencyContactId
        launchSubmission {
            saveEmergencyContact(
                profileId,
                contactId,
                EmergencyContactInput(name, relationship, phone)
            ).fold(
                onSuccess = { saved ->
                    updateState {
                        copy(
                            isSubmitting = false,
                            emergencyContactId = saved.id,
                            emergencyContactName = saved.contactName,
                            emergencyRelationship = saved.relationship?.toEmergencyRelationship(),
                            emergencyPhoneNumber = saved.phoneNumber,
                            emergencyContacts = emergencyContacts.filterNot { it.id == saved.id } + saved,
                            validationErrors = validationErrors - EMERGENCY_FIELDS
                        )
                    }
                    finishHealthOnboarding()
                },
                onFailure = { failure ->
                    if ((failure as? ProfileException)?.statusCode == 409 && contactId == null) {
                        reconcileEmergencyConflict(profileId, failure)
                    } else {
                        finishFailure(failure)
                    }
                }
            )
        }
    }

    private suspend fun reconcileEmergencyConflict(profileId: String, conflict: Throwable) {
        loadEmergencyContacts(profileId).fold(
            onSuccess = { contacts ->
                updateState {
                    hydrateContacts(contacts).copy(
                        isSubmitting = false,
                        errorMessage = conflict.userMessage()
                    )
                }
            },
            onFailure = ::finishFailure
        )
    }

    private fun finishHealthOnboarding() {
        if (currentState.isInitializing || currentState.isSubmitting) return
        val userKey = requireUserKey() ?: return
        launchSubmission {
            // The handled flag is best-effort local state. It must never block Skip or final navigation.
            markOnboardingHandled(userKey)
            updateState { copy(isSubmitting = false) }
            sendEffect(ProfileCompletionEffect.NavigateToHome)
        }
    }

    private fun moveTo(step: ProfileStep) {
        updateState { copy(currentStep = step, errorMessage = null) }
        ensureStepLoaded(step)
    }

    private fun ensureStepLoaded(step: ProfileStep) {
        if (step in currentState.loadedSteps || step !in REMOTE_STEPS) return
        val profileId = requireProfileId() ?: return
        updateState { copy(isLoadingStep = true, errorMessage = null) }
        viewModelScope.launch {
            when (step) {
                ProfileStep.MedicalConditions -> loadMedicalConditions(profileId).fold(
                    onSuccess = { data ->
                        val remoteSaved = data.saved.map {
                            ProfileCatalogOption(
                                it.conditionName.normalizedCatalogKey(),
                                it.conditionName
                            )
                        }
                        val options = (data.catalog.map {
                            ProfileCatalogOption(it.localKey, it.name)
                        } + remoteSaved).distinctBy { it.localKey }
                        val savedKeys = data.saved.map { it.conditionName.normalizedCatalogKey() }.toSet()
                        updateState {
                            copy(
                                isLoadingStep = false,
                                conditionCatalog = options,
                                selectedConditionKeys = savedKeys,
                                originalConditionBackendIds = data.saved.map { it.medicalConditionId }.toSet(),
                                loadedSteps = loadedSteps + step
                            )
                        }
                    },
                    onFailure = ::finishLoadFailure
                )
                ProfileStep.Allergies -> loadAllergies(profileId).fold(
                    onSuccess = { data ->
                        val remoteSaved = data.saved.map {
                            ProfileAllergyOption(
                                it.allergyName.normalizedCatalogKey(),
                                it.allergyName,
                                it.type
                            )
                        }
                        val options = (data.catalog.map {
                            ProfileAllergyOption(it.localKey, it.name, it.type)
                        } + remoteSaved).distinctBy { it.localKey }
                        val savedKeys = data.saved.map { it.allergyName.normalizedCatalogKey() }.toSet()
                        updateState {
                            copy(
                                isLoadingStep = false,
                                allergyCatalog = options,
                                selectedAllergyKeys = if (hasNoKnownAllergies) emptySet() else savedKeys,
                                originalAllergyBackendIds = data.saved.map { it.allergyId }.toSet(),
                                loadedSteps = loadedSteps + step
                            )
                        }
                    },
                    onFailure = ::finishLoadFailure
                )
                ProfileStep.EmergencyContact -> loadEmergencyContacts(profileId).fold(
                    onSuccess = { contacts ->
                        updateState {
                            hydrateContacts(contacts).copy(
                                isLoadingStep = false,
                                emergencyContactsLoaded = true,
                                loadedSteps = loadedSteps + step
                            )
                        }
                    },
                    onFailure = ::finishLoadFailure
                )
                else -> updateState { copy(isLoadingStep = false, loadedSteps = loadedSteps + step) }
            }
        }
    }

    private fun navigateBack() {
        if (currentState.isSubmitting) return
        when (currentState.currentStep) {
            ProfileStep.Welcome -> sendEffect(ProfileCompletionEffect.NavigateBack)
            ProfileStep.BasicHealthInfo -> moveTo(ProfileStep.Welcome)
            ProfileStep.MedicalConditions -> moveTo(ProfileStep.BasicHealthInfo)
            ProfileStep.Allergies -> moveTo(ProfileStep.MedicalConditions)
            ProfileStep.CurrentMedications -> moveTo(ProfileStep.Allergies)
            ProfileStep.MedicalHistory -> moveTo(ProfileStep.CurrentMedications)
            ProfileStep.MobilityStatus -> moveTo(ProfileStep.MedicalHistory)
            ProfileStep.EmergencyContact, ProfileStep.FinalStep -> moveTo(ProfileStep.MobilityStatus)
        }
    }

    private fun retry() {
        when {
            currentState.profileId == null -> {
                updateState { copy(initialized = false, errorMessage = null) }
                initialize()
            }
            currentState.currentStep in REMOTE_STEPS && currentState.currentStep !in currentState.loadedSteps ->
                ensureStepLoaded(currentState.currentStep)
            else -> continueFlow()
        }
    }

    private fun launchSubmission(block: suspend () -> Unit) {
        if (currentState.isSubmitting) return
        updateState { copy(isSubmitting = true, errorMessage = null) }
        viewModelScope.launch { block() }
    }

    private fun finishFailure(error: Throwable) = updateState {
        copy(isSubmitting = false, errorMessage = error.userMessage())
    }

    private fun finishLoadFailure(error: Throwable) = updateState {
        copy(isLoadingStep = false, errorMessage = error.userMessage())
    }

    private fun showError(message: String) = updateState { copy(errorMessage = message) }

    private fun showValidationErrors(
        sectionFields: Set<ProfileField>,
        errors: Map<ProfileField, ProfileValidationError>
    ) = updateState {
        copy(
            validationErrors = (validationErrors - sectionFields) + errors,
            errorMessage = null
        )
    }

    private fun requireProfileId(): String? = currentState.profileId ?: run {
        showError("Profile information is unavailable")
        null
    }

    private fun requireUserKey(): String? = currentState.userKey ?: run {
        showError("A valid user profile is required")
        null
    }

    private fun edit(
        vararg fields: ProfileField,
        transform: ProfileCompletionState.() -> ProfileCompletionState
    ) = updateState {
        val updated = transform()
        updated.copy(
            errorMessage = null,
            validationErrors = updated.validationErrors - fields.toSet()
        )
    }

    private fun updateMedication(
        index: Int,
        transform: LocalMedicationEntry.() -> LocalMedicationEntry
    ) = updateState {
        if (index !in currentMedications.indices) return@updateState this
        val entry = currentMedications[index]
        val updatedErrors = medicationValidationErrors.toMutableMap()
        updatedErrors[entry.localId]?.let { currentErrors ->
            val cleared = currentErrors.copy(name = null)
            if (cleared.isEmpty) updatedErrors.remove(entry.localId) else updatedErrors[entry.localId] = cleared
        }
        copy(
            hasNoCurrentMedications = false,
            currentMedications = currentMedications.toMutableList().apply {
                this[index] = this[index].transform()
            },
            errorMessage = null,
            validationErrors = validationErrors - ProfileField.MedicationsSelection,
            medicationValidationErrors = updatedErrors
        )
    }

    private fun ProfileCompletionState.hydrateProfile(profile: Profile): ProfileCompletionState = copy(
        profile = profile,
        profileId = profile.id,
        height = profile.height?.displayNumber() ?: height,
        weight = profile.weight?.displayNumber() ?: weight,
        bloodType = profile.bloodType?.replace('−', '-') ?: bloodType,
        previousSurgeries = profile.previousSurgeries.orEmpty(),
        previousHospitalizations = profile.previousHospitalizations.orEmpty()
    )

    private fun ProfileCompletionState.hydrateContacts(contacts: List<EmergencyContact>): ProfileCompletionState {
        val editable = contacts.singleOrNull()
        return copy(
            emergencyContacts = contacts,
            emergencyContactsLoaded = true,
            emergencyContactId = editable?.id,
            emergencyContactName = editable?.contactName.orEmpty(),
            emergencyRelationship = editable?.relationship?.toEmergencyRelationship(),
            emergencyPhoneNumber = editable?.phoneNumber.orEmpty()
        )
    }

    private fun Set<String>.toggle(value: String) = if (value in this) this - value else this + value
    private fun Throwable.userMessage() = message ?: "Something went wrong. Please try again."
    private fun Double.displayNumber() = if (this % 1.0 == 0.0) toInt().toString() else toString()

    private companion object {
        const val MAX_MEDICATIONS = 10
        val BASIC_HEALTH_FIELDS = setOf(ProfileField.Height, ProfileField.Weight, ProfileField.BloodType)
        val MEDICAL_CONDITION_FIELDS = setOf(ProfileField.OtherConditions)
        val ALLERGY_FIELDS = setOf(ProfileField.AllergiesSelection, ProfileField.OtherAllergies)
        val MEDICATION_FIELDS = setOf(ProfileField.MedicationsSelection)
        val MEDICAL_HISTORY_FIELDS = setOf(
            ProfileField.PreviousSurgeries,
            ProfileField.PreviousHospitalizations
        )
        val MOBILITY_FIELDS = setOf(ProfileField.MobilityStatus, ProfileField.MobilityNotes)
        val EMERGENCY_FIELDS = setOf(
            ProfileField.EmergencyContactName,
            ProfileField.EmergencyRelationship,
            ProfileField.EmergencyPhoneNumber
        )
        val REMOTE_STEPS = setOf(
            ProfileStep.MedicalConditions,
            ProfileStep.Allergies,
            ProfileStep.EmergencyContact
        )
    }

}


private fun temporaryDebugProfile(profileId: String) = Profile(
    id = profileId,
    userId = null,
    relationship = null,
    firstName = null,
    lastName = null,
    dateOfBirth = null,
    gender = null,
    bloodType = null,
    height = null,
    weight = null,
    mobilityStatus = null,
    mobilityNotes = null,
    previousSurgeries = null,
    previousHospitalizations = null
)

private fun blankMedication() = LocalMedicationEntry(localId = "local-${UUID.randomUUID()}")

private fun String.normalizedCatalogKey(): String =
    trim().lowercase().replace(Regex("[^a-z0-9]+"), "-").trim('-')

private fun MobilityStatus.wireValue() = when (this) {
    MobilityStatus.Independent -> "INDEPENDENT"
    MobilityStatus.NeedsAssistance -> "NEEDS_ASSISTANCE"
    MobilityStatus.UsesWalkingAid -> "USES_WALKING_AID"
    MobilityStatus.WheelchairUser -> "WHEELCHAIR_USER"
    MobilityStatus.Bedridden -> "BEDRIDDEN"
}

private fun String.toMobilityStatus() = MobilityStatus.entries.firstOrNull {
    it.wireValue().equals(this, ignoreCase = true) ||
        it.name.equals(this, ignoreCase = true) ||
        it.wireValue().replace('_', ' ').equals(this, ignoreCase = true)
}

private fun EmergencyRelationship.wireValue() = when (this) {
    EmergencyRelationship.Spouse -> "Spouse"
    EmergencyRelationship.Parent -> "Parent"
    EmergencyRelationship.Sibling -> "Sibling"
    EmergencyRelationship.AdultChild -> "Adult Child"
    EmergencyRelationship.FriendOrNeighbor -> "Friend / Neighbor"
    EmergencyRelationship.Other -> "Other"
}

private fun String.toEmergencyRelationship() = EmergencyRelationship.entries.firstOrNull {
    it.wireValue().equals(this, ignoreCase = true) || it.name.equals(this, ignoreCase = true)
}
