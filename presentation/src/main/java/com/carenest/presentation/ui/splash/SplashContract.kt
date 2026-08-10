package com.carenest.presentation.ui.splash


data class SplashState(
    val isLoading: Boolean = true
)

sealed interface SplashIntent {
    data object OnStart : SplashIntent
}

sealed interface SplashEffect {
    data object NavigateToOnBoarding : SplashEffect
    data object NavigateToHome : SplashEffect
    data object NavigateToLogin : SplashEffect
    data object NavigateToRegister : SplashEffect
    data object NavigateToCompleteProfile : SplashEffect
    data class NavigateToTracking(val requestId: String) : SplashEffect
    data class NavigateToSearch(val requestId: String) : SplashEffect
}
