package com.carenest.presentation.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class SplashViewModel @Inject constructor() :
    ViewModel(),
    StateHolder<SplashState> by DefaultStateHolder(SplashState()),
    EffectPublisher<SplashEffect> by DefaultEffectPublisher() {

    init {
        onIntent(SplashIntent.OnStart)
    }

    fun onIntent(intent: SplashIntent) {
        when (intent) {
            is SplashIntent.OnStart -> handleStart()
        }
    }

    private fun handleStart() {
        viewModelScope.launch {
            delay(2000.milliseconds)
            sendEffect(SplashEffect.NavigateToOnBoarding)
        }
    }
}
