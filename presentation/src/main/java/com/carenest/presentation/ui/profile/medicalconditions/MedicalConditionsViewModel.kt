package com.carenest.presentation.ui.profile.medicalconditions

import androidx.lifecycle.ViewModel
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder

class MedicalConditionsViewModel : ViewModel(),
    StateHolder<MedicalConditionsState> by DefaultStateHolder(MedicalConditionsState()),
    EffectPublisher<MedicalConditionsEffect> by DefaultEffectPublisher() {

    fun onEvent(event: MedicalConditionsIntent) {
        when (event) {
            is MedicalConditionsIntent.ConditionToggled -> updateState {
                copy(selectedConditions = selectedConditions.toggle(event.condition))
            }
            is MedicalConditionsIntent.OtherConditionsChanged ->
                updateState { copy(otherConditions = event.conditions) }
            MedicalConditionsIntent.BackClicked -> sendEffect(MedicalConditionsEffect.NavigateBack)
            MedicalConditionsIntent.ContinueClicked ->
                sendEffect(MedicalConditionsEffect.NavigateToAllergies)
        }
    }

    private fun Set<String>.toggle(value: String): Set<String> =
        if (value in this) this - value else this + value
}
