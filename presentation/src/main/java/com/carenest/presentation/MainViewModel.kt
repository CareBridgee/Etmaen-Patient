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
import com.carenest.domain.model.ThemeMode
import com.carenest.domain.socket.SocketConnectionManager
import com.carenest.domain.socket.SocketError
import com.carenest.domain.socket.SocketServiceController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.carenest.domain.repository.SettingsRepository

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getOnboardingStatusUseCase: GetOnboardingStatusUseCase,
    private val getLoggedInStatusUseCase: GetLoggedInStatusUseCase,
    private val settingsRepository: SettingsRepository,
    private val socketServiceController: SocketServiceController,
    private val socketConnectionManager: SocketConnectionManager,
    private val getSettingsUseCase: GetSettingsUseCase,
) : ViewModel(),
    StateHolder<MainState> by DefaultStateHolder(MainState()),
    EffectPublisher<MainEffect> by DefaultEffectPublisher() {

    init {
        observeAppState()
        observeSocketErrors()
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

    private fun observeSocketErrors() {
        viewModelScope.launch {
            val lastNoticeAt = mutableMapOf<String, Long>()
            socketConnectionManager.socketErrors.collect { error ->
                when (error) {
                    is SocketError.AuthFailed -> onSocketAuthFailed()
                    is SocketError.NetworkError, SocketError.HeartbeatTimeout -> {
                        if (shouldEmit(lastNoticeAt, error::class.simpleName.orEmpty())) {
                            sendEffect(MainEffect.ConnectionNotice(SOCKET_RECONNECTING_MESSAGE))
                        }
                    }
                    is SocketError.StompError -> {
                        if (shouldEmit(lastNoticeAt, "stomp:${error.message}")) {
                            sendEffect(
                                MainEffect.SocketAlert(
                                    title = "Connection error",
                                    message = error.message
                                )
                            )
                        }
                    }
                    is SocketError.OperationError -> {
                        if (shouldEmit(lastNoticeAt, "op:${error.code}")) {
                            sendEffect(
                                MainEffect.SocketAlert(
                                    title = "Something went wrong",
                                    message = error.message
                                )
                            )
                        }
                    }
                    is SocketError.SubscriptionError, is SocketError.ParsingError -> Unit
                }
            }
        }
    }

    private suspend fun onSocketAuthFailed() {
        val stillLoggedIn = getLoggedInStatusUseCase().first()
        if (stillLoggedIn) {
            socketServiceController.startService()
            sendEffect(
                MainEffect.SocketAlert(
                    title = "Connection problem",
                    message = "We couldn't verify your session with the server. Retrying…"
                )
            )
        } else {
            sendEffect(
                MainEffect.SocketAlert(
                    title = "Session expired",
                    message = "Please sign in again to continue."
                )
            )
        }
    }

    private fun shouldEmit(lastNoticeAt: MutableMap<String, Long>, key: String): Boolean {
        val now = System.currentTimeMillis()
        val last = lastNoticeAt[key] ?: 0L
        if (now - last < NOTICE_THROTTLE_MS) return false
        lastNoticeAt[key] = now
        return true
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
    data class ToggleTheme(val themeMode:ThemeMode) : MainIntent
    data object ResetApp : MainIntent
}

sealed interface MainEffect {
    data class SocketAlert(val title: String, val message: String) : MainEffect
    data class ConnectionNotice(val message: String) : MainEffect
}

private const val NOTICE_THROTTLE_MS = 10_000L
private const val SOCKET_RECONNECTING_MESSAGE = "Connection lost — reconnecting…"
