package com.carenest.presentation.ui.aichat.choosepatient

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.domain.usecase.family_members.GetFamilyMembersUseCase
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.carenest.domain.usecase.user.ObserveCurrentUserUseCase

@HiltViewModel
class ChoosePatientViewModel @Inject constructor(
    private val getFamilyMembersUseCase: GetFamilyMembersUseCase,
    private val observeCurrentUserUseCase: ObserveCurrentUserUseCase
) : ViewModel(),
    StateHolder<ChoosePatientState> by DefaultStateHolder(ChoosePatientState()),
    EffectPublisher<ChoosePatientEffect> by DefaultEffectPublisher() {

    init {
        observeUser()
        loadPatients()
    }

    private fun observeUser() {
        viewModelScope.launch {
            observeCurrentUserUseCase().collect { user ->
                updateState {
                    copy(
                        userName = user?.name.orEmpty(),
                        userAvatarUrl = user?.profileImageUrl?.takeIf(String::isNotBlank)
                    )
                }
            }
        }
    }

    fun loadPatients() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }

            val patientsList = mutableListOf<PatientItem>()

            getFamilyMembersUseCase().onSuccess { members ->
                members.forEachIndexed { index, member ->
                    if (!member.isDeleted && patientsList.none { it.id == member.id }) {
                        val isSelf = member.isPrimary ||
                                member.relationship.isNullOrBlank() ||
                                member.relationship.equals("Self", ignoreCase = true) ||
                                member.relationship.equals("PRIMARY", ignoreCase = true) ||
                                member.relationship.equals("Primary", ignoreCase = true)
                        val relationshipLabel = if (isSelf) "Self" else (member.relationship ?: "Member")
                        patientsList.add(
                            PatientItem(
                                id = member.id,
                                name = member.fullName,
                                relationship = relationshipLabel,
                                isSelected = index == 0
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
