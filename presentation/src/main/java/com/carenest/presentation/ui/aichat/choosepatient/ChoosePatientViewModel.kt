package com.carenest.presentation.ui.aichat.choosepatient

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.domain.usecase.familymembers.GetFamilyMembersUseCase
import com.carenest.domain.usecase.user.GetCurrentUserUseCase
import com.carenest.domain.usecase.user.ObserveCurrentUserUseCase
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChoosePatientViewModel @Inject constructor(
    private val getFamilyMembersUseCase: GetFamilyMembersUseCase,
    private val observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
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
                        userAvatarUrl = user?.profileImageUrl?.takeIf(String::isNotBlank),
                        patients = patients.map { patient ->
                            val isSelf = patient.id == user?.defaultProfileId ||
                                patient.relationship.equals("Self", ignoreCase = true)
                            if (isSelf && !user?.name.isNullOrBlank()) {
                                patient.copy(name = user.name.orEmpty())
                            } else {
                                patient
                            }
                        },
                    )
                }
            }
        }
    }

    fun loadPatients() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }

            val cachedUser = observeCurrentUserUseCase().first()
            val currentUser = getCurrentUserUseCase().getOrNull() ?: cachedUser
            val currentUserName = currentUser?.name.orEmpty().ifBlank { currentState.userName }
            val patientsList = mutableListOf<PatientItem>()

            getFamilyMembersUseCase().onSuccess { members ->
                val sortedMembers = members.sortedByDescending { member ->
                    member.isPrimary ||
                            member.id == currentUser?.defaultProfileId ||
                            member.relationship.isNullOrBlank() ||
                            member.relationship.equals("Self", ignoreCase = true) ||
                            member.relationship.equals("PRIMARY", ignoreCase = true) ||
                            member.relationship.equals("Primary", ignoreCase = true)
                }
                sortedMembers.forEachIndexed { index, member ->
                    if (!member.isDeleted && patientsList.none { it.id == member.id }) {
                        val isSelf = member.isPrimary ||
                                member.id == currentUser?.defaultProfileId ||
                                member.relationship.isNullOrBlank() ||
                                member.relationship.equals("Self", ignoreCase = true) ||
                                member.relationship.equals("PRIMARY", ignoreCase = true) ||
                                member.relationship.equals("Primary", ignoreCase = true)
                        val relationshipLabel = if (isSelf) "Self" else (member.relationship ?: "Member")

                        val profileName = listOfNotNull(member.firstName, member.lastName)
                            .filter { it.isNotBlank() }
                            .joinToString(" ")
                        val displayName = if (isSelf) {
                            currentUserName.ifBlank { profileName.ifBlank { "User" } }
                        } else {
                            member.fullName
                        }

                        patientsList.add(
                            PatientItem(
                                id = member.id,
                                name = displayName,
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
