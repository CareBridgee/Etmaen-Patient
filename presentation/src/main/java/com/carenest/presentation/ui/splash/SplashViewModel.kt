package com.carenest.presentation.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.domain.usecase.settings.GetLoggedInStatusUseCase
import com.carenest.domain.usecase.settings.GetOnboardingStatusUseCase
import com.carenest.domain.model.user.AuthenticatedDestination
import com.carenest.domain.usecase.user.RefreshAuthenticatedSessionUseCase
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getOnboardingStatusUseCase: GetOnboardingStatusUseCase,
    private val getLoggedInStatusUseCase: GetLoggedInStatusUseCase,
    private val refreshSession: RefreshAuthenticatedSessionUseCase,
    private val getCurrentNurseTrackingInfoUseCase: com.carenest.domain.usecase.tracking.GetCurrentNurseTrackingInfoUseCase
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
                isLoggedIn -> refreshSession().fold(
                    onSuccess = { destination ->
                        if (destination == AuthenticatedDestination.Home) {
                            getCurrentNurseTrackingInfoUseCase()
                                .onSuccess { info ->
                                    val isSearching = info.status.equals("SEARCHING", ignoreCase = true) || 
                                                     info.status.equals("PENDING", ignoreCase = true)
                                    
                                    if (isSearching) {
                                        sendEffect(SplashEffect.NavigateToSearch(info.requestId))
                                    } else {
                                        sendEffect(SplashEffect.NavigateToTracking(info.requestId))
                                    }
                                }
                                .onFailure {
                                    sendEffect(SplashEffect.NavigateToHome)
                                }
                        } else {
                            sendEffect(
                                when (destination) {
                                    AuthenticatedDestination.Registration -> SplashEffect.NavigateToRegister
                                    AuthenticatedDestination.CompleteProfile -> SplashEffect.NavigateToCompleteProfile
                                    AuthenticatedDestination.Home -> SplashEffect.NavigateToHome
                                }
                            )
                        }
                    },
                    onFailure = { sendEffect(SplashEffect.NavigateToLogin) }
                )
                else -> sendEffect(SplashEffect.NavigateToLogin)
            }
        }
    }
}
