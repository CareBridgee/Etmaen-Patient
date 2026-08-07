package com.carenest.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.domain.usecase.settings.GetLoggedInStatusUseCase
import com.carenest.domain.usecase.settings.GetOnboardingStatusUseCase
import com.carenest.domain.usecase.settings.GetSettingsUseCase
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import com.carenest.domain.model.settings.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.carenest.domain.repository.SettingsRepository

import com.carenest.domain.socket.SocketServiceController
import kotlinx.coroutines.flow.collect

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getOnboardingStatusUseCase: GetOnboardingStatusUseCase,
    private val getLoggedInStatusUseCase: GetLoggedInStatusUseCase,
    private val settingsRepository: SettingsRepository,
    private val socketServiceController: SocketServiceController
    private val getSettingsUseCase: GetSettingsUseCase,
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
                if (isLoggedIn) {
                    socketServiceController.startService()
                } else {
                    socketServiceController.stopService()
                }

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
            is MainIntent.ChangeLanguage -> updateState { copy(languageCode = intent.languageCode) }
            is MainIntent.ToggleTheme -> updateState { copy(themeMode = intent.themeMode) }
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
    data class ChangeLanguage(val languageCode: String) : MainIntent
    data object ToggleTheme(val themeMode:ThemeMode) : MainIntent
    data object ResetApp : MainIntent
}

sealed interface MainEffect
