package com.carenest.presentation.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.domain.socket.ConnectionState
import com.carenest.domain.socket.SocketConnectionManager
import com.carenest.domain.socket.SocketServiceController
import com.carenest.domain.usecase.settings.GetSettingsUseCase
import com.carenest.domain.usecase.settings.UpdateSettingsUseCase
import com.carenest.domain.usecase.user.ObserveCurrentUserUseCase
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getSettingsUseCase: GetSettingsUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase,
    private val observeCurrentUser: ObserveCurrentUserUseCase,
    private val socketServiceController: SocketServiceController,
    private val socketConnectionManager: SocketConnectionManager
) : ViewModel(),
    StateHolder<SettingsState> by DefaultStateHolder(SettingsState()),
    EffectPublisher<SettingsEffect> by DefaultEffectPublisher() {

    init {
        viewModelScope.launch {
            observeCurrentUser().collect { user ->
                updateState { copy(patientName = user?.name.orEmpty()) }
            }
        }
        viewModelScope.launch {
            getSettingsUseCase().collect { settings ->
                updateState {
                    copy(
                        languageCode = settings.languageCode,
                        themeMode = settings.themeMode
                    )
                }
            }
        }
        viewModelScope.launch {
            socketConnectionManager.connectionState.collect { connectionState ->
                val isConnectedOrConnecting = when (connectionState) {
                    is ConnectionState.Connected,
                    is ConnectionState.Connecting,
                    is ConnectionState.Reconnecting -> true
                    else -> false
                }
                updateState { copy(isBackgroundServiceEnabled = isConnectedOrConnecting) }
            }
        }
    }

    fun onEvent(event: SettingsEvent) {
        when (event) {
            SettingsEvent.OnBackClicked -> {
                sendEffect(SettingsEffect.NavigateBack)
            }
            SettingsEvent.OnLanguageClicked -> {
                updateState { copy(isLanguagePickerDialogVisible = true) }
            }
            is SettingsEvent.OnLanguageSelected -> {
                updateState { copy(isLanguagePickerDialogVisible = false) }
                if (event.languageCode in SUPPORTED_LANGUAGES) {
                    persist { updateSettingsUseCase.updateLanguage(event.languageCode) }
                }
            }
            SettingsEvent.OnDismissLanguagePicker -> {
                updateState { copy(isLanguagePickerDialogVisible = false) }
            }
            SettingsEvent.OnThemeClicked -> {
                updateState { copy(isThemePickerDialogVisible = true) }
            }
            is SettingsEvent.OnThemeSelected -> {
                updateState { copy(isThemePickerDialogVisible = false) }
                persist { updateSettingsUseCase.updateThemeMode(event.themeMode) }
            }
            SettingsEvent.OnDismissThemePicker -> {
                updateState { copy(isThemePickerDialogVisible = false) }
            }
            SettingsEvent.OnTermsClicked ->
                sendEffect(SettingsEffect.ShowMessage(SettingsMessage.TermsUnavailable))
            SettingsEvent.OnDeleteAccountClicked ->
                sendEffect(SettingsEffect.ShowMessage(SettingsMessage.DeleteAccountUnavailable))
            SettingsEvent.OnContactSupportClicked ->
                sendEffect(SettingsEffect.ShowMessage(SettingsMessage.ContactSupportUnavailable))
            SettingsEvent.OnStopServiceClicked -> {
                socketServiceController.stopService()
                socketConnectionManager.disconnect()
                sendEffect(SettingsEffect.ShowMessage(SettingsMessage.ServiceStopped))
            }
            is SettingsEvent.OnToggleService -> {
                if (event.enabled) {
                    socketServiceController.startService()
                    sendEffect(SettingsEffect.ShowMessage(SettingsMessage.ServiceEnabled))
                } else {
                    socketServiceController.stopService()
                    socketConnectionManager.disconnect()
                    sendEffect(SettingsEffect.ShowMessage(SettingsMessage.ServiceDisabled))
                }
            }
        }
    }

    private fun persist(action: suspend () -> Unit) {
        if (currentState.isLoading) return
        updateState { copy(isLoading = true) }
        viewModelScope.launch {
            runCatching { action() }.fold(
                onSuccess = { updateState { copy(isLoading = false) } },
                onFailure = {
                    updateState { copy(isLoading = false) }
                    sendEffect(SettingsEffect.ShowMessage(SettingsMessage.SaveFailed))
                }
            )
        }
    }

    private companion object {
        val SUPPORTED_LANGUAGES = setOf("ar", "en")
    }
}
