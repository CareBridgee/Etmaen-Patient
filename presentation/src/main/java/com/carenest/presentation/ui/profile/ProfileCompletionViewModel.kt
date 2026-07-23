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
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileCompletionViewModel @Inject constructor(
    private val getDefaultProfile: GetDefaultProfileUseCase,
    private val updateBasicHealth: UpdateBasicHealthUseCase,
    private val updateMedicalHistory: UpdateMedicalHistoryUseCase,
    private val updateMobility: UpdateMobilityUseCase,
    private val loadMedicalConditions: LoadMedicalConditionsUseCase,
    private val syncMedicalConditions: SyncMedicalConditionsUseCase,
    private val loadAllergies: LoadAllergiesUseCase,
    private val syncAllergies: SyncAllergiesUseCase,
    private val loadMedications: LoadMedicationsUseCase,
    private val syncMedications: SyncMedicationsUseCase,
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
            is ProfileCompletionIntent.HeightChanged -> edit { copy(height = event.height) }
            is ProfileCompletionIntent.WeightChanged -> edit { copy(weight = event.weight) }
            is ProfileCompletionIntent.BloodTypeChanged -> edit { copy(bloodType = event.bloodType) }
            is ProfileCompletionIntent.ConditionToggled -> edit {
                copy(selectedConditionKeys = selectedConditionKeys.toggle(event.localKey))
            }
            is ProfileCompletionIntent.OtherConditionsChanged -> edit { copy(otherConditions = event.conditions) }
            ProfileCompletionIntent.NoKnownAllergiesToggled -> edit {
                val none = !hasNoKnownAllergies
                copy(
                    hasNoKnownAllergies = none,
                    selectedAllergyKeys = if (none) emptySet() else selectedAllergyKeys,
                    otherAllergies = if (none) "" else otherAllergies
                )
            }
            is ProfileCompletionIntent.AllergyToggled -> edit {
                copy(
                    hasNoKnownAllergies = false,
                    selectedAllergyKeys = selectedAllergyKeys.toggle(event.localKey)
                )
            }
            is ProfileCompletionIntent.OtherAllergiesChanged -> edit {
                copy(hasNoKnownAllergies = false, otherAllergies = event.allergies)
            }
            ProfileCompletionIntent.NoCurrentMedicationsToggled -> edit {
                val none = !hasNoCurrentMedications
                copy(
                    hasNoCurrentMedications = none,
                    currentMedications = if (none) emptyList() else listOf(blankMedication())
                )
            }
            ProfileCompletionIntent.MedicationAdded -> edit {
                if (hasNoCurrentMedications) this else copy(currentMedications = currentMedications + blankMedication())
            }
            is ProfileCompletionIntent.MedicationNameChanged -> updateMedication(event.index) { copy(name = event.value) }
            is ProfileCompletionIntent.MedicationDosageChanged -> updateMedication(event.index) { copy(dosage = event.value) }
            is ProfileCompletionIntent.MedicationFrequencyChanged -> updateMedication(event.index) { copy(frequency = event.value) }
            is ProfileCompletionIntent.MedicationRemoved -> edit {
                if (event.index !in currentMedications.indices) this else copy(
                    currentMedications = currentMedications.filterIndexed { index, _ -> index != event.index }
                )
            }
            is ProfileCompletionIntent.PreviousSurgeriesChanged -> edit { copy(previousSurgeries = event.surgeries) }
            is ProfileCompletionIntent.PreviousHospitalizationsChanged -> edit {
                copy(previousHospitalizations = event.hospitalizations)
            }
            is ProfileCompletionIntent.MobilityStatusSelected -> edit { copy(mobilityStatus = event.status) }
            is ProfileCompletionIntent.MobilityNotesChanged -> edit { copy(mobilityNotes = event.notes) }
            is ProfileCompletionIntent.EmergencyContactNameChanged -> edit { copy(emergencyContactName = event.name) }
            is ProfileCompletionIntent.EmergencyRelationshipSelected -> edit {
                copy(emergencyRelationship = event.relationship)
            }
            is ProfileCompletionIntent.EmergencyPhoneNumberChanged -> edit {
                copy(emergencyPhoneNumber = event.phoneNumber)
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
                        selectedConditionKeys = draft.selectedConditionKeys,
                        otherConditions = draft.otherConditions,
                        selectedAllergyKeys = draft.selectedAllergyKeys,
                        otherAllergies = draft.otherAllergies,
                        hasNoKnownAllergies = draft.noKnownAllergiesConfirmed,
                        currentMedications = draft.medications,
                        hasNoCurrentMedications = draft.noCurrentMedicationsConfirmed,
                        mobilityStatus = draft.pendingMobilityStatus?.toMobilityStatus()
                            ?: profile.mobilityStatus?.toMobilityStatus(),
                        mobilityNotes = draft.pendingMobilityNotes ?: profile.mobilityNotes.orEmpty()
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
        val height = currentState.height.toDoubleOrNull()
        val weight = currentState.weight.toDoubleOrNull()
        val bloodType = currentState.bloodType.replace('−', '-')
        if (height == null || !height.isFinite() || height <= 0 ||
            weight == null || !weight.isFinite() || weight <= 0 || bloodType !in BLOOD_TYPES
        ) return showError("Enter a valid height, weight, and blood type")
        val profileId = requireProfileId() ?: return
        launchSubmission {
            updateBasicHealth(profileId, BasicHealthUpdate(height, weight, bloodType)).fold(
                onSuccess = { profile ->
                    updateState { hydrateProfile(profile).copy(isSubmitting = false) }
                    moveTo(ProfileStep.MedicalConditions)
                },
                onFailure = ::finishFailure
            )
        }
    }

    private fun submitMedicalConditions() {
        val profileId = requireProfileId() ?: return
        val userKey = requireUserKey() ?: return
        val snapshot = currentState
        val draft = snapshot.localDraft.copy(
            selectedConditionKeys = snapshot.selectedConditionKeys,
            otherConditions = snapshot.otherConditions
        )
        launchSubmission {
            saveProfileDraft(userKey, draft).getOrElse { finishFailure(it); return@launchSubmission }
            val sync = syncMedicalConditions(
                profileId,
                snapshot.originalConditionBackendIds,
                snapshot.selectedConditionKeys
            )
            updateState {
                copy(
                    isSubmitting = false,
                    localDraft = draft,
                    originalConditionBackendIds = sync.getOrNull() ?: originalConditionBackendIds
                )
            }
            moveTo(ProfileStep.Allergies)
        }
    }

    private fun submitAllergies() {
        val profileId = requireProfileId() ?: return
        val userKey = requireUserKey() ?: return
        val snapshot = currentState
        val selectedKeys = if (snapshot.hasNoKnownAllergies) emptySet() else snapshot.selectedAllergyKeys
        val draft = snapshot.localDraft.copy(
            selectedAllergyKeys = selectedKeys,
            otherAllergies = if (snapshot.hasNoKnownAllergies) "" else snapshot.otherAllergies,
            noKnownAllergiesConfirmed = snapshot.hasNoKnownAllergies
        )
        launchSubmission {
            saveProfileDraft(userKey, draft).getOrElse { finishFailure(it); return@launchSubmission }
            val sync = syncAllergies(profileId, snapshot.originalAllergyBackendIds, selectedKeys)
            updateState {
                copy(
                    isSubmitting = false,
                    localDraft = draft,
                    selectedAllergyKeys = selectedKeys,
                    originalAllergyBackendIds = sync.getOrNull() ?: originalAllergyBackendIds
                )
            }
            moveTo(ProfileStep.CurrentMedications)
        }
    }

    private fun submitMedications() {
        val profileId = requireProfileId() ?: return
        val userKey = requireUserKey() ?: return
        val snapshot = currentState
        val entries = if (snapshot.hasNoCurrentMedications) emptyList() else {
            snapshot.currentMedications
                .filter { it.name.isNotBlank() || it.dosage.isNotBlank() || it.frequency.isNotBlank() }
                .map { it.copy(syncState = if (it.backendMedicationId == null) SyncState.LOCAL_ONLY else SyncState.PENDING) }
        }
        val draft = snapshot.localDraft.copy(
            medications = entries,
            noCurrentMedicationsConfirmed = snapshot.hasNoCurrentMedications
        )
        launchSubmission {
            saveProfileDraft(userKey, draft).getOrElse { finishFailure(it); return@launchSubmission }
            val sync = syncMedications(profileId, snapshot.originalMedicationBackendIds, entries)
            val storedEntries = sync.getOrElse {
                entries.map { entry ->
                    if (entry.backendMedicationId == null) entry else entry.copy(syncState = SyncState.FAILED)
                }
            }
            val storedDraft = draft.copy(medications = storedEntries)
            saveProfileDraft(userKey, storedDraft).getOrElse {
                finishFailure(it)
                return@launchSubmission
            }
            updateState {
                copy(
                    isSubmitting = false,
                    localDraft = storedDraft,
                    currentMedications = storedEntries,
                    originalMedicationBackendIds = if (sync.isSuccess) {
                        storedEntries.mapNotNull { it.backendMedicationId }.toSet()
                    } else originalMedicationBackendIds
                )
            }
            moveTo(ProfileStep.MedicalHistory)
        }
    }

    private fun submitMedicalHistory() {
        val profileId = requireProfileId() ?: return
        val snapshot = currentState
        launchSubmission {
            updateMedicalHistory(
                profileId,
                MedicalHistoryUpdate(snapshot.previousSurgeries, snapshot.previousHospitalizations)
            ).fold(
                onSuccess = { profile ->
                    updateState { hydrateProfile(profile).copy(isSubmitting = false) }
                    moveTo(ProfileStep.MobilityStatus)
                },
                onFailure = ::finishFailure
            )
        }
    }

    private fun submitMobility() {
        val status = currentState.mobilityStatus ?: return showError("Select a mobility status")
        val profileId = requireProfileId() ?: return
        val userKey = requireUserKey() ?: return
        val snapshot = currentState
        val pendingDraft = snapshot.localDraft.copy(
            pendingMobilityStatus = status.wireValue(),
            pendingMobilityNotes = snapshot.mobilityNotes
        )
        launchSubmission {
            saveProfileDraft(userKey, pendingDraft).getOrElse { finishFailure(it); return@launchSubmission }
            val result = updateMobility(profileId, MobilityUpdate(status.wireValue(), snapshot.mobilityNotes))
            if (result.isSuccess) {
                val cleared = pendingDraft.copy(pendingMobilityStatus = null, pendingMobilityNotes = null)
                saveProfileDraft(userKey, cleared)
                updateState {
                    hydrateProfile(result.getOrThrow()).copy(isSubmitting = false, localDraft = cleared)
                }
            } else {
                updateState { copy(isSubmitting = false, localDraft = pendingDraft) }
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
        val name = currentState.emergencyContactName.trim()
        val phone = currentState.emergencyPhoneNumber.trim()
        if (name.isBlank() || name.length > 100 || !PHONE_REGEX.matches(phone)) {
            return showError("Enter a valid emergency contact name and phone number")
        }
        val relationship = currentState.emergencyRelationship?.wireValue()
        if ((relationship?.length ?: 0) > 100) return showError("Emergency relationship is too long")
        val profileId = requireProfileId() ?: return
        val contactId = currentState.emergencyContactId
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
                            emergencyContacts = emergencyContacts.filterNot { it.id == saved.id } + saved
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
                                it.conditionName,
                                CatalogSource.REMOTE
                            )
                        }
                        val options = (data.catalog.map {
                            ProfileCatalogOption(it.localKey, it.name, it.source)
                        } + remoteSaved).distinctBy { it.localKey }
                        val savedKeys = data.saved.map { it.conditionName.normalizedCatalogKey() }.toSet()
                        updateState {
                            copy(
                                isLoadingStep = false,
                                conditionCatalog = options,
                                selectedConditionKeys = selectedConditionKeys + savedKeys,
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
                                it.type,
                                CatalogSource.REMOTE
                            )
                        }
                        val options = (data.catalog.map {
                            ProfileAllergyOption(it.localKey, it.name, it.type, it.source)
                        } + remoteSaved).distinctBy { it.localKey }
                        val savedKeys = data.saved.map { it.allergyName.normalizedCatalogKey() }.toSet()
                        updateState {
                            copy(
                                isLoadingStep = false,
                                allergyCatalog = options,
                                selectedAllergyKeys = if (hasNoKnownAllergies) emptySet() else selectedAllergyKeys + savedKeys,
                                originalAllergyBackendIds = data.saved.map { it.allergyId }.toSet(),
                                loadedSteps = loadedSteps + step
                            )
                        }
                    },
                    onFailure = ::finishLoadFailure
                )
                ProfileStep.CurrentMedications -> loadMedications(profileId).fold(
                    onSuccess = { data ->
                        val options = data.catalog.map {
                            ProfileCatalogOption(it.localKey, it.name, it.source)
                        }
                        val localEntries = currentState.currentMedications
                        val localByBackendId = localEntries.mapNotNull {
                            it.backendMedicationId?.let { id -> id to it }
                        }.toMap()
                        val remoteEntries = data.saved.map { saved ->
                            localByBackendId[saved.medicationId] ?: LocalMedicationEntry(
                                localId = "remote-${saved.medicationId}",
                                backendMedicationId = saved.medicationId,
                                name = saved.medicationName,
                                syncState = SyncState.SYNCED
                            )
                        }
                        val merged = (localEntries + remoteEntries).distinctBy { it.localId }
                        updateState {
                            copy(
                                isLoadingStep = false,
                                medicationCatalog = options,
                                currentMedications = if (
                                    hasNoCurrentMedications || merged.isNotEmpty()
                                ) merged else listOf(blankMedication()),
                                originalMedicationBackendIds = data.saved.map { it.medicationId }.toSet(),
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

    private fun requireProfileId(): String? = currentState.profileId ?: run {
        showError("Profile information is unavailable")
        null
    }

    private fun requireUserKey(): String? = currentState.userKey ?: run {
        showError("A valid user profile is required")
        null
    }

    private fun edit(transform: ProfileCompletionState.() -> ProfileCompletionState) =
        updateState { transform().copy(errorMessage = null) }

    private fun updateMedication(
        index: Int,
        transform: LocalMedicationEntry.() -> LocalMedicationEntry
    ) = edit {
        if (index !in currentMedications.indices) this else copy(
            hasNoCurrentMedications = false,
            currentMedications = currentMedications.toMutableList().apply {
                this[index] = this[index].transform()
            }
        )
    }

    private fun ProfileCompletionState.hydrateProfile(profile: Profile): ProfileCompletionState = copy(
        profile = profile,
        profileId = profile.id,
        height = profile.height?.displayNumber() ?: height,
        weight = profile.weight?.displayNumber() ?: weight,
        bloodType = profile.bloodType?.replace('−', '-') ?: bloodType,
        previousSurgeries = profile.previousSurgeries.orEmpty(),
        previousHospitalizations = profile.previousHospitalizations.orEmpty(),
        mobilityStatus = profile.mobilityStatus?.toMobilityStatus() ?: mobilityStatus,
        mobilityNotes = profile.mobilityNotes ?: mobilityNotes
    )

    private fun ProfileCompletionState.hydrateContacts(contacts: List<EmergencyContact>): ProfileCompletionState {
        val editable = contacts.singleOrNull()
        return copy(
            emergencyContacts = contacts,
            emergencyContactsLoaded = true,
            emergencyContactId = editable?.id,
            emergencyContactName = editable?.contactName.orEmpty(),
            emergencyRelationship = editable?.relationship?.toEmergencyRelationship(),
            emergencyPhoneNumber = editable?.phoneNumber.orEmpty(),
            errorMessage = if (contacts.size > 1) {
                "Multiple emergency contacts exist; select a contact before editing. Editing is unavailable in this screen."
            } else errorMessage
        )
    }

    private fun Set<String>.toggle(value: String) = if (value in this) this - value else this + value
    private fun Throwable.userMessage() = message ?: "Something went wrong. Please try again."
    private fun Double.displayNumber() = if (this % 1.0 == 0.0) toInt().toString() else toString()

    private companion object {
        val BLOOD_TYPES = setOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
        val PHONE_REGEX = Regex("^\\+?[0-9\\s\\-]{7,20}$")
        val REMOTE_STEPS = setOf(
            ProfileStep.MedicalConditions,
            ProfileStep.Allergies,
            ProfileStep.CurrentMedications,
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
