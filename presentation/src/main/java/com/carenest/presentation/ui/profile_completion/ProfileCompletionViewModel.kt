package com.carenest.presentation.ui.profile_completion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.domain.model.profile.EmergencyContact
import com.carenest.domain.model.profile.EmergencyRelationship
import com.carenest.domain.model.profile.MedicationInput
import com.carenest.domain.model.profile.MobilityStatus
import com.carenest.domain.model.profile.Profile
import com.carenest.domain.model.profile.ProfileField
import com.carenest.domain.model.profile.ProfileValidationError
import com.carenest.domain.model.profile.ProfileValidationException
import com.carenest.domain.usecase.profile.GetDefaultProfileUseCase
import com.carenest.domain.usecase.profile.LoadAllergiesUseCase
import com.carenest.domain.usecase.profile.LoadEmergencyContactsUseCase
import com.carenest.domain.usecase.profile.LoadMedicalConditionsUseCase
import com.carenest.domain.usecase.profile.LoadMedicationsUseCase
import com.carenest.domain.usecase.profile.SaveEmergencyContactUseCase
import com.carenest.domain.usecase.profile.SyncAllergiesUseCase
import com.carenest.domain.usecase.profile.SyncMedicalConditionsUseCase
import com.carenest.domain.usecase.profile.SyncMedicationsUseCase
import com.carenest.domain.usecase.profile.UpdateBasicHealthUseCase
import com.carenest.domain.usecase.profile.UpdateMedicalHistoryUseCase
import com.carenest.domain.usecase.profile.UpdateMobilityUseCase
import com.carenest.domain.validation.EgyptianPhoneNumberValidator
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val loadMedications: LoadMedicationsUseCase,
    private val syncMedications: SyncMedicationsUseCase,
    private val updateMobility: UpdateMobilityUseCase,
    private val loadEmergencyContacts: LoadEmergencyContactsUseCase,
    private val saveEmergencyContact: SaveEmergencyContactUseCase
) : ViewModel(),
    StateHolder<ProfileCompletionState> by DefaultStateHolder(ProfileCompletionState()),
    EffectPublisher<ProfileCompletionEffect> by DefaultEffectPublisher() {

    private var nextMedicationUiKey = 0L

    init {
        initialize()
    }

    fun onEvent(event: ProfileCompletionIntent) {
        when (event) {
            ProfileCompletionIntent.ConfigureEditMode -> updateState {
                copy(isEditMode = true, currentStep = ProfileStep.BasicHealthInfo)
            }
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
                copy(selectedConditionIds = selectedConditionIds.toggle(event.id))
            }
            is ProfileCompletionIntent.OtherConditionsChanged -> edit(ProfileField.OtherConditions) {
                copy(otherConditions = event.conditions.take(500))
            }
            ProfileCompletionIntent.NoKnownAllergiesToggled -> edit(ProfileField.AllergiesSelection) {
                val none = !hasNoKnownAllergies
                copy(
                    hasNoKnownAllergies = none,
                    selectedAllergyIds = if (none) emptySet() else selectedAllergyIds,
                    otherAllergies = if (none) "" else otherAllergies,
                    validationErrors = validationErrors - ProfileField.OtherAllergies
                )
            }
            is ProfileCompletionIntent.AllergyToggled -> edit(ProfileField.AllergiesSelection) {
                copy(
                    hasNoKnownAllergies = false,
                    selectedAllergyIds = selectedAllergyIds.toggle(event.id)
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
                val hasIncompleteMedication = currentMedications.any { it.name.isBlank() }
                if (
                    hasNoCurrentMedications ||
                    currentMedications.size >= MAX_MEDICATIONS ||
                    hasIncompleteMedication
                ) {
                    this
                } else {
                    copy(currentMedications = currentMedications + blankMedication())
                }
            }
            is ProfileCompletionIntent.MedicationNameChanged -> updateMedication(event.index) {
                copy(name = event.value.take(100))
            }
            is ProfileCompletionIntent.MedicationRemoved -> edit(ProfileField.MedicationsSelection) {
                if (event.index !in currentMedications.indices) {
                    this
                } else {
                    val removedKey = currentMedications[event.index].uiKey
                    copy(
                        currentMedications = currentMedications.filterIndexed { index, _ ->
                            index != event.index
                        },
                        medicationValidationErrors = medicationValidationErrors - removedKey
                    )
                }
            }
            is ProfileCompletionIntent.PreviousSurgeriesChanged -> edit(ProfileField.PreviousSurgeries) {
                copy(previousSurgeries = event.surgeries.take(1000))
            }
            is ProfileCompletionIntent.PreviousHospitalizationsChanged ->
                edit(ProfileField.PreviousHospitalizations) {
                    copy(previousHospitalizations = event.hospitalizations.take(1000))
                }
            is ProfileCompletionIntent.MobilityStatusSelected -> edit(ProfileField.MobilityStatus) {
                copy(mobilityStatus = event.status)
            }
            is ProfileCompletionIntent.MobilityNotesChanged -> edit(ProfileField.MobilityNotes) {
                copy(mobilityNotes = event.notes.take(500))
            }
            is ProfileCompletionIntent.EmergencyContactNameChanged ->
                edit(ProfileField.EmergencyContactName) {
                    copy(emergencyContactName = event.name.take(100))
                }
            is ProfileCompletionIntent.EmergencyRelationshipSelected ->
                edit(ProfileField.EmergencyRelationship) {
                    copy(emergencyRelationship = event.relationship)
                }
            is ProfileCompletionIntent.EmergencyPhoneNumberChanged ->
                edit(ProfileField.EmergencyPhoneNumber) {
                    copy(
                        emergencyPhoneNumber = EgyptianPhoneNumberValidator.sanitizeInput(
                            event.phoneNumber
                        )
                    )
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
            val profile = getDefaultProfile().getOrElse {
                updateState { copy(isInitializing = false, errorMessage = it.userMessage()) }
                return@launch
            }
            val contactsResult = loadEmergencyContacts(profile.id)
            updateState {
                hydrateProfile(profile)
                    .hydrateContacts(contactsResult.getOrDefault(emptyList()))
                    .copy(
                        isInitializing = false,
                        currentMedications = if (currentMedications.isEmpty()) {
                            listOf(blankMedication())
                        } else {
                            currentMedications
                        },
                        emergencyContactsLoaded = contactsResult.isSuccess,
                        loadedSteps = if (contactsResult.isSuccess) {
                            loadedSteps + ProfileStep.EmergencyContact
                        } else {
                            loadedSteps
                        }
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
        val profileId = requireProfileId() ?: return
        val snapshot = currentState
        launchSubmission {
            updateBasicHealth(
                profileId = profileId,
                height = snapshot.height,
                weight = snapshot.weight,
                bloodType = snapshot.bloodType
            ).fold(
                onSuccess = { profile ->
                    updateState {
                        hydrateProfile(profile).copy(
                            isSubmitting = false,
                            validationErrors = validationErrors - BASIC_HEALTH_FIELDS
                        )
                    }
                    moveTo(ProfileStep.MedicalConditions)
                },
                onFailure = { handleFailure(it, BASIC_HEALTH_FIELDS) }
            )
        }
    }

    private fun submitMedicalConditions() {
        val profileId = requireProfileId() ?: return
        val snapshot = currentState
        launchSubmission {
            syncMedicalConditions(
                profileId = profileId,
                selectedBackendIds = snapshot.selectedConditionIds,
                otherConditions = snapshot.otherConditions
            ).fold(
                onSuccess = { _ ->
                    updateState {
                        copy(
                            isSubmitting = false,
                            otherConditions = otherConditions.trim(),
                            validationErrors = validationErrors - MEDICAL_CONDITION_FIELDS
                        )
                    }
                    moveTo(ProfileStep.Allergies)
                },
                onFailure = { handleFailure(it, MEDICAL_CONDITION_FIELDS) }
            )
        }
    }

    private fun submitAllergies() {
        val profileId = requireProfileId() ?: return
        val snapshot = currentState
        launchSubmission {
            syncAllergies(
                profileId = profileId,
                selectedBackendIds = snapshot.selectedAllergyIds,
                hasNoKnownAllergies = snapshot.hasNoKnownAllergies,
                otherAllergies = snapshot.otherAllergies
            ).fold(
                onSuccess = { syncedIds ->
                    updateState {
                        copy(
                            isSubmitting = false,
                            selectedAllergyIds = syncedIds,
                            otherAllergies = if (hasNoKnownAllergies) "" else otherAllergies.trim(),
                            validationErrors = validationErrors - ALLERGY_FIELDS
                        )
                    }
                    moveTo(ProfileStep.CurrentMedications)
                },
                onFailure = { handleFailure(it, ALLERGY_FIELDS) }
            )
        }
    }

    private fun submitMedications() {
        val profileId = requireProfileId() ?: return
        val snapshot = currentState
        launchSubmission {
            syncMedications(
                profileId = profileId,
                hasNoCurrentMedications = snapshot.hasNoCurrentMedications,
                entries = snapshot.currentMedications
            ).fold(
                onSuccess = { names ->
                    updateState {
                        copy(
                            isSubmitting = false,
                            currentMedications = names.map { name ->
                                MedicationInput(uiKey = nextMedicationUiKey++, name = name)
                            },
                            hasNoCurrentMedications = names.isEmpty(),
                            validationErrors = validationErrors - MEDICATION_FIELDS,
                            medicationValidationErrors = emptyMap()
                        )
                    }
                    moveTo(ProfileStep.MedicalHistory)
                },
                onFailure = { handleFailure(it, MEDICATION_FIELDS) }
            )
        }
    }

    private fun submitMedicalHistory() {
        val profileId = requireProfileId() ?: return
        val snapshot = currentState
        launchSubmission {
            updateMedicalHistory(
                profileId = profileId,
                previousSurgeries = snapshot.previousSurgeries,
                previousHospitalizations = snapshot.previousHospitalizations
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
                onFailure = { handleFailure(it, MEDICAL_HISTORY_FIELDS) }
            )
        }
    }

    private fun submitMobility() {
        val profileId = requireProfileId() ?: return
        val snapshot = currentState
        launchSubmission {
            updateMobility(profileId, snapshot.mobilityStatus, snapshot.mobilityNotes).fold(
                onSuccess = { profile ->
                    updateState {
                        hydrateProfile(profile).copy(
                            isSubmitting = false,
                            validationErrors = validationErrors - MOBILITY_FIELDS
                        )
                    }
                    moveTo(ProfileStep.EmergencyContact)
                },
                onFailure = { handleFailure(it, MOBILITY_FIELDS) }
            )
        }
    }

    private fun submitEmergencyContact() {
        if (!currentState.emergencyContactsLoaded) {
            ensureStepLoaded(ProfileStep.EmergencyContact)
            return
        }
        val profileId = requireProfileId() ?: return
        val snapshot = currentState
        launchSubmission {
            saveEmergencyContact(
                profileId = profileId,
                emergencyContactId = snapshot.emergencyContactId,
                contactName = snapshot.emergencyContactName,
                relationship = snapshot.emergencyRelationship,
                phoneNumber = snapshot.emergencyPhoneNumber
            ).fold(
                onSuccess = { saved ->
                    updateState {
                        copy(
                            isSubmitting = false,
                            emergencyContactId = saved.id,
                            emergencyContactName = saved.contactName,
                            emergencyRelationship = EmergencyRelationship.fromBackend(saved.relationship),
                            emergencyPhoneNumber = saved.phoneNumber,
                            emergencyContacts = emergencyContacts.filterNot { it.id == saved.id } + saved,
                            validationErrors = validationErrors - EMERGENCY_FIELDS
                        )
                    }
                    finishHealthOnboarding()
                },
                onFailure = { failure ->
                    handleFailure(failure, EMERGENCY_FIELDS)
                }
            )
        }
    }


    private fun finishHealthOnboarding() {
        if (currentState.isInitializing || currentState.isSubmitting) return
        sendEffect(
            if (currentState.isEditMode) ProfileCompletionEffect.NavigateAfterEdit
            else ProfileCompletionEffect.NavigateToHome
        )
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
                        val catalogIds = data.catalog.mapTo(hashSetOf()) { it.id }
                        val options = data.catalog.map { ProfileCatalogOption(it.id, it.name) }
                        updateState {
                            copy(
                                isLoadingStep = false,
                                conditionCatalog = options,
                                selectedConditionIds = data.saved.filter {
                                    it.medicalConditionId in catalogIds
                                }.map {
                                    it.medicalConditionId
                                }.toSet(),
                                otherConditions = data.saved.filter {
                                    it.medicalConditionId !in catalogIds
                                }.joinToString(", ") { it.conditionName },
                                loadedSteps = loadedSteps + step
                            )
                        }
                    },
                    onFailure = ::finishLoadFailure
                )
                ProfileStep.Allergies -> loadAllergies(profileId).fold(
                    onSuccess = { data ->
                        val catalogIds = data.catalog.mapTo(hashSetOf()) { it.id }
                        val options = data.catalog.map {
                            ProfileAllergyOption(it.id, it.name, it.type)
                        }
                        updateState {
                            copy(
                                isLoadingStep = false,
                                allergyCatalog = options,
                                selectedAllergyIds = data.saved.filter {
                                    it.allergyId in catalogIds
                                }.mapTo(linkedSetOf()) { it.allergyId },
                                otherAllergies = data.saved.filter {
                                    it.allergyId !in catalogIds
                                }.joinToString(", ") { it.allergyName },
                                hasNoKnownAllergies = data.saved.isEmpty(),
                                loadedSteps = loadedSteps + step
                            )
                        }
                    },
                    onFailure = ::finishLoadFailure
                )
                ProfileStep.CurrentMedications -> loadMedications(profileId).fold(
                    onSuccess = { medications ->
                        updateState {
                            copy(
                                isLoadingStep = false,
                                currentMedications = medications.map { medication ->
                                    MedicationInput(
                                        uiKey = nextMedicationUiKey++,
                                        name = medication.name
                                    )
                                },
                                hasNoCurrentMedications = medications.isEmpty(),
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
                else -> updateState {
                    copy(isLoadingStep = false, loadedSteps = loadedSteps + step)
                }
            }
        }
    }

    private fun navigateBack() {
        if (currentState.isSubmitting) return
        when (currentState.currentStep) {
            ProfileStep.Welcome -> sendEffect(ProfileCompletionEffect.NavigateBack)
            ProfileStep.BasicHealthInfo -> if (currentState.isEditMode) {
                sendEffect(ProfileCompletionEffect.NavigateBack)
            } else {
                moveTo(ProfileStep.Welcome)
            }
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
            currentState.currentStep in REMOTE_STEPS &&
                currentState.currentStep !in currentState.loadedSteps ->
                ensureStepLoaded(currentState.currentStep)
            else -> continueFlow()
        }
    }

    private fun launchSubmission(block: suspend () -> Unit) {
        if (currentState.isSubmitting) return
        updateState { copy(isSubmitting = true, errorMessage = null) }
        viewModelScope.launch { block() }
    }

    private fun handleFailure(error: Throwable, sectionFields: Set<ProfileField>) {
        val validation = error as? ProfileValidationException
        if (validation == null) {
            finishFailure(error)
            return
        }
        updateState {
            copy(
                isSubmitting = false,
                errorMessage = null,
                validationErrors = (validationErrors - sectionFields) + validation.fieldErrors,
                medicationValidationErrors = if (ProfileField.MedicationsSelection in sectionFields) {
                    validation.medicationErrors
                } else {
                    medicationValidationErrors
                }
            )
        }
    }

    private fun finishFailure(error: Throwable) = updateState {
        copy(isSubmitting = false, errorMessage = error.userMessage())
    }

    private fun finishLoadFailure(error: Throwable) = updateState {
        copy(isLoadingStep = false, errorMessage = error.userMessage())
    }

    private fun showError(message: String) = updateState { copy(errorMessage = message) }

    private fun requireProfileId(): String? = currentState.profileId ?: run {
        showError("Profile information is unavailable")
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
        transform: MedicationInput.() -> MedicationInput
    ) = updateState {
        if (index !in currentMedications.indices) return@updateState this
        val entry = currentMedications[index]
        val updatedErrors = medicationValidationErrors.toMutableMap()
        updatedErrors[entry.uiKey]?.let { currentErrors ->
            val cleared = currentErrors.copy(name = null)
            if (cleared.isEmpty) {
                updatedErrors.remove(entry.uiKey)
            } else {
                updatedErrors[entry.uiKey] = cleared
            }
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
        height = profile.height?.displayNumber().orEmpty(),
        weight = profile.weight?.displayNumber().orEmpty(),
        bloodType = profile.bloodType?.replace('−', '-').orEmpty(),
        previousSurgeries = profile.previousSurgeries.orEmpty(),
        previousHospitalizations = profile.previousHospitalizations.orEmpty(),
        mobilityStatus = profile.mobilityStatus?.let { value ->
            MobilityStatus.entries.firstOrNull { it.name.equals(value, ignoreCase = true) }
        },
        mobilityNotes = profile.mobilityNotes.orEmpty()
    )

    private fun ProfileCompletionState.hydrateContacts(
        contacts: List<EmergencyContact>
    ): ProfileCompletionState {
        val editable = contacts.firstOrNull()
        return copy(
            emergencyContacts = contacts,
            emergencyContactsLoaded = true,
            emergencyContactId = editable?.id,
            emergencyContactName = editable?.contactName.orEmpty(),
            emergencyRelationship = EmergencyRelationship.fromBackend(editable?.relationship),
            emergencyPhoneNumber = editable?.phoneNumber.orEmpty()
        )
    }

    private fun Set<String>.toggle(value: String): Set<String> =
        if (value in this) this - value else this + value

    private fun Throwable.userMessage(): String = "profile_operation_failed"

    private fun Double.displayNumber(): String =
        if (this % 1.0 == 0.0) toInt().toString() else toString()

    private fun blankMedication(): MedicationInput =
        MedicationInput(uiKey = nextMedicationUiKey++)

    private companion object {
        const val MAX_MEDICATIONS = 10
        val BASIC_HEALTH_FIELDS = setOf(
            ProfileField.Height,
            ProfileField.Weight,
            ProfileField.BloodType
        )
        val MEDICAL_CONDITION_FIELDS = setOf(ProfileField.OtherConditions)
        val ALLERGY_FIELDS = setOf(
            ProfileField.AllergiesSelection,
            ProfileField.OtherAllergies
        )
        val MEDICATION_FIELDS = setOf(ProfileField.MedicationsSelection)
        val MEDICAL_HISTORY_FIELDS = setOf(
            ProfileField.PreviousSurgeries,
            ProfileField.PreviousHospitalizations
        )
        val MOBILITY_FIELDS = setOf(
            ProfileField.MobilityStatus,
            ProfileField.MobilityNotes
        )
        val EMERGENCY_FIELDS = setOf(
            ProfileField.EmergencyContactName,
            ProfileField.EmergencyRelationship,
            ProfileField.EmergencyPhoneNumber
        )
        val REMOTE_STEPS = setOf(
            ProfileStep.MedicalConditions,
            ProfileStep.Allergies,
            ProfileStep.CurrentMedications,
            ProfileStep.EmergencyContact
        )
    }
}
