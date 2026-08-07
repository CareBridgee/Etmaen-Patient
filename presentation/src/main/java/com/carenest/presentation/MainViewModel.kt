package com.carenest.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.domain.usecase.settings.GetLoggedInStatusUseCase
import com.carenest.domain.usecase.settings.GetOnboardingStatusUseCase
import com.carenest.domain.usecase.settings.GetSettingsUseCase
import com.carenest.domain.model.settings.ThemeMode
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.carenest.domain.repository.SettingsRepository

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getOnboardingStatusUseCase: GetOnboardingStatusUseCase,
    private val getLoggedInStatusUseCase: GetLoggedInStatusUseCase,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val settingsRepository: SettingsRepository
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
                getLoggedInStatusUseCase(),
                getSettingsUseCase()
            ) { onboardingDone, isLoggedIn, settings ->
                Log.d("MainViewModel", "observeAppState: onboardingDone=$onboardingDone, isLoggedIn=$isLoggedIn")
                Triple(onboardingDone, isLoggedIn, settings)
            }.collect { (onboardingDone, isLoggedIn, settings) ->
                updateState {
                    copy(
                        onboardingDone = onboardingDone,
                        isLoggedIn = isLoggedIn,
                        isReady = true,
                        themeMode = settings.themeMode,
                        languageCode = settings.languageCode
                    )
                }
            }
        }
    }

    fun onIntent(intent: MainIntent) {
        when (intent) {
            MainIntent.ResetApp -> viewModelScope.launch {
                settingsRepository.updateOnboardingStatus(false)
                settingsRepository.updateLoggedInStatus(false)
                Log.d("MainViewModel", "App state reset requested")
            }
        }
    }
}

data class MainState(
    val onboardingDone: Boolean = false,
    val isLoggedIn: Boolean = false,
    val isReady: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val languageCode: String = "en"
)

sealed interface MainIntent {
    data object ResetApp : MainIntent
}

sealed interface MainEffect
