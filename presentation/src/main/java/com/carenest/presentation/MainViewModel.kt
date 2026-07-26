package com.carenest.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.domain.usecase.settings.GetLoggedInStatusUseCase
import com.carenest.domain.usecase.settings.GetOnboardingStatusUseCase
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getOnboardingStatusUseCase: GetOnboardingStatusUseCase,
    private val getLoggedInStatusUseCase: GetLoggedInStatusUseCase
) : ViewModel(),
    StateHolder<MainState> by DefaultStateHolder(MainState()),
    EffectPublisher<MainEffect> by DefaultEffectPublisher() {

    init {
        observeAppState()
    }

    private fun observeAppState() {
        viewModelScope.launch {
            combine(
                getOnboardingStatusUseCase(),
                getLoggedInStatusUseCase()
            ) { onboardingDone, isLoggedIn ->
                updateState {
                    copy(
                        onboardingDone = onboardingDone,
                        isLoggedIn = isLoggedIn,
                        isReady = true
                    )
                }
            }.collect {}
        }
    }

    fun onIntent(intent: MainIntent) {
        when (intent) {
            is MainIntent.ChangeLanguage -> updateState { copy(languageCode = intent.languageCode) }
            is MainIntent.ToggleTheme -> updateState { copy(isDarkTheme = !isDarkTheme) }
        }
    }
}

data class MainState(
    val onboardingDone: Boolean = false,
    val isLoggedIn: Boolean = false,
    val isReady: Boolean = false,
    val isDarkTheme: Boolean = false,
    val languageCode: String = "en"
)

sealed interface MainIntent {
    data class ChangeLanguage(val languageCode: String) : MainIntent
    data object ToggleTheme : MainIntent
}

sealed interface MainEffect
