package com.carenest.presentation.ui.aichat.choosepatient

import androidx.lifecycle.ViewModel
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChoosePatientViewModel @Inject constructor() : ViewModel(),
    StateHolder<ChoosePatientState> by DefaultStateHolder(
        ChoosePatientState(
            patients = listOf(
                PatientItem(id = "p_1", name = "Elena Rodriguez", relationship = "Self", isSelected = true),
                PatientItem(id = "p_2", name = "Robert Chen", relationship = "Dad", isSelected = false),
                PatientItem(id = "p_3", name = "Margaret Chen", relationship = "Mom", isSelected = false)
            )
        )
    ),
    EffectPublisher<ChoosePatientEffect> by DefaultEffectPublisher() {

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
                // Not implemented yet
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
