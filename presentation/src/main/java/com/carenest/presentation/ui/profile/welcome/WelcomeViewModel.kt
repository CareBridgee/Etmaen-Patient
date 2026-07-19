package com.carenest.presentation.ui.profile.welcome

import androidx.lifecycle.ViewModel
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder

class WelcomeViewModel : ViewModel(),
    StateHolder<WelcomeState> by DefaultStateHolder(WelcomeState),
    EffectPublisher<WelcomeEffect> by DefaultEffectPublisher() {

    fun onEvent(event: WelcomeIntent) {
        when (event) {
            WelcomeIntent.BackClicked -> sendEffect(WelcomeEffect.NavigateBack)
            WelcomeIntent.CompleteProfileClicked -> sendEffect(WelcomeEffect.NavigateToPersonalInfo)
            WelcomeIntent.SkipClicked -> sendEffect(WelcomeEffect.SkipProfile)
        }
    }
}
