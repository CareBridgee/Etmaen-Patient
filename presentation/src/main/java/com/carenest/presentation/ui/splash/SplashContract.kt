package com.carenest.presentation.ui.splash


data class SplashState(
    val isLoading: Boolean = true
)

sealed interface SplashIntent {
    data object OnStart : SplashIntent
}

sealed interface SplashEffect {
    data object NavigateToOnBoarding : SplashEffect
}
