package com.carenest.presentation.ui.profile.welcome

data object WelcomeState

sealed interface WelcomeIntent {
    data object BackClicked : WelcomeIntent
    data object CompleteProfileClicked : WelcomeIntent
    data object SkipClicked : WelcomeIntent
}

sealed interface WelcomeEffect {
    data object NavigateBack : WelcomeEffect
    data object NavigateToPersonalInfo : WelcomeEffect
    data object SkipProfile : WelcomeEffect
}
