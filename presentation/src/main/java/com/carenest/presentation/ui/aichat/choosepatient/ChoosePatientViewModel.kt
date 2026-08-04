package com.carenest.presentation.ui.aichat.choosepatient

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.domain.usecase.family_members.GetFamilyMembersUseCase
import com.carenest.domain.usecase.profile.GetDefaultProfileUseCase
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChoosePatientViewModel @Inject constructor(
    private val getDefaultProfileUseCase: GetDefaultProfileUseCase,
    private val getFamilyMembersUseCase: GetFamilyMembersUseCase
) : ViewModel(),
    StateHolder<ChoosePatientState> by DefaultStateHolder(ChoosePatientState()),
    EffectPublisher<ChoosePatientEffect> by DefaultEffectPublisher() {

    init {
        loadPatients()
    }

    fun loadPatients() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }

            val patientsList = mutableListOf<PatientItem>()

            // First load default profile (Self)
            getDefaultProfileUseCase().onSuccess { defaultProfile ->
                val fullName = listOfNotNull(defaultProfile.firstName, defaultProfile.lastName)
                    .joinToString(" ")
                    .ifBlank { "Self" }
                patientsList.add(
                    PatientItem(
                        id = defaultProfile.id,
                        name = fullName,
                        relationship = "Self",
                        isSelected = true
                    )
                )
            }

            // Then load family members
            getFamilyMembersUseCase().onSuccess { members ->
                members.forEach { member ->
                    if (patientsList.none { it.id == member.id }) {
                        val name = member.contactName.ifBlank { member.relationship ?: "Family Member" }
                        patientsList.add(
                            PatientItem(
                                id = member.id,
                                name = name,
                                relationship = member.relationship ?: "Member",
                                isSelected = false
                            )
                        )
                    }
                }
            }

            updateState {
                copy(
                    patients = if (patientsList.isNotEmpty()) patientsList else patients,
                    isLoading = false
                )
            }
        }
    }

    fun onEvent(event: ChoosePatientEvent) {
        when (event) {
            is ChoosePatientEvent.OnPatientSelected -> {
                updateState {
                    copy(
                        patients = patients.map {
                            it.copy(isSelected = it.id == event.patientId)
                        }
                    )
                }
            }
            is ChoosePatientEvent.OnAddFamilyMemberClicked -> {
                sendEffect(ChoosePatientEffect.NavigateToAddFamilyMember)
            }
            is ChoosePatientEvent.OnContinueClicked -> {
                val selectedPatient = currentState.patients.find { it.isSelected }
                selectedPatient?.let {
                    sendEffect(ChoosePatientEffect.NavigateToChat(it.id))
                }
            }
        }
    }
}
