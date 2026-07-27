package com.carenest.presentation.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.domain.usecase.settings.GetLoggedInStatusUseCase
import com.carenest.domain.usecase.settings.GetOnboardingStatusUseCase
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

import com.carenest.domain.usecase.settings.GetLoggedInStatusUseCase
import com.carenest.domain.usecase.settings.GetOnboardingStatusUseCase
import kotlinx.coroutines.flow.first

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getOnboardingStatusUseCase: GetOnboardingStatusUseCase,
    private val getLoggedInStatusUseCase: GetLoggedInStatusUseCase
) :
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
            val onboardingDone = getOnboardingStatusUseCase().first()
            val isLoggedIn = getLoggedInStatusUseCase().first()


            when {
                !onboardingDone -> sendEffect(SplashEffect.NavigateToOnBoarding)
                isLoggedIn -> sendEffect(SplashEffect.NavigateToHome)
                else -> sendEffect(SplashEffect.NavigateToLogin)
            }
        }
    }
}
