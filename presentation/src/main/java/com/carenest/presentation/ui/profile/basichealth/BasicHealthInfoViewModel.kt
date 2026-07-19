package com.carenest.presentation.ui.profile.basichealth

import androidx.lifecycle.ViewModel
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder

class BasicHealthInfoViewModel : ViewModel(),
    StateHolder<BasicHealthInfoState> by DefaultStateHolder(BasicHealthInfoState()),
    EffectPublisher<BasicHealthInfoEffect> by DefaultEffectPublisher() {

    fun onEvent(event: BasicHealthInfoIntent) {
        when (event) {
            is BasicHealthInfoIntent.HeightChanged -> updateState { copy(height = event.height) }
            is BasicHealthInfoIntent.WeightChanged -> updateState { copy(weight = event.weight) }
            is BasicHealthInfoIntent.BloodTypeChanged -> updateState { copy(bloodType = event.bloodType) }
            BasicHealthInfoIntent.BackClicked -> sendEffect(BasicHealthInfoEffect.NavigateBack)
            BasicHealthInfoIntent.ContinueClicked ->
                sendEffect(BasicHealthInfoEffect.NavigateToMedicalConditions)
        }
    }
}
