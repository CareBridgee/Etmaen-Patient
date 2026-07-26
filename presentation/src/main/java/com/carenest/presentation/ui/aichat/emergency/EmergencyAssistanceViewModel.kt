package com.carenest.presentation.ui.aichat.emergency

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EmergencyAssistanceViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel(),
    StateHolder<EmergencyAssistanceState> by DefaultStateHolder(EmergencyAssistanceState()),
    EffectPublisher<EmergencyAssistanceEffect> by DefaultEffectPublisher() {

    fun onEvent(event: EmergencyAssistanceEvent) {
        when (event) {
            is EmergencyAssistanceEvent.OnBackClicked -> {
                sendEffect(EmergencyAssistanceEffect.NavigateBack)
            }
            is EmergencyAssistanceEvent.OnCallAmbulanceClicked -> {
                sendEffect(EmergencyAssistanceEffect.CallAmbulance)
            }
            is EmergencyAssistanceEvent.OnCallEmergencyContactClicked -> {
                sendEffect(EmergencyAssistanceEffect.CallEmergencyContact)
            }
            is EmergencyAssistanceEvent.OnDismissClicked -> {
                sendEffect(EmergencyAssistanceEffect.DismissEmergency)
            }
            is EmergencyAssistanceEvent.OnInputTextChanged -> {
                updateState { copy(inputText = event.text) }
            }
            is EmergencyAssistanceEvent.OnSendMessage -> {
                if (currentState.inputText.isNotBlank()) {
                    updateState { copy(inputText = "") }
                }
            }
        }
    }
}
