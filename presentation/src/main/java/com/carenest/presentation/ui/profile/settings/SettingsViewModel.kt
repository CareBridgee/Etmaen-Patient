package com.carenest.presentation.ui.profile.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.domain.usecase.settings.GetSettingsUseCase
import com.carenest.domain.usecase.settings.UpdateSettingsUseCase
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
    private val updateSettingsUseCase: UpdateSettingsUseCase
) : ViewModel(),
    StateHolder<SettingsState> by DefaultStateHolder(SettingsState()),
    EffectPublisher<SettingsEffect> by DefaultEffectPublisher() {

    init {
        viewModelScope.launch {
            getSettingsUseCase().collect { settings ->
                updateState {
                    copy(
                        languageCode = settings.languageCode,
                        isDarkMode = settings.isDarkMode,
                        emailUpdatesEnabled = settings.emailUpdatesEnabled,
                        smsAlertsEnabled = settings.smsAlertsEnabled
                    )
                }
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
                viewModelScope.launch {
                    updateSettingsUseCase.updateLanguage(event.languageCode)
                }
            }
            SettingsEvent.OnDismissLanguagePicker -> {
                updateState { copy(isLanguagePickerDialogVisible = false) }
            }
            is SettingsEvent.OnDarkModeToggled -> {
                viewModelScope.launch {
                    updateSettingsUseCase.updateDarkMode(event.enabled)
                }
            }
            is SettingsEvent.OnEmailUpdatesToggled -> {
                viewModelScope.launch {
                    updateSettingsUseCase.updateEmailUpdates(event.enabled)
                }
            }
            is SettingsEvent.OnSmsAlertsToggled -> {
                viewModelScope.launch {
                    updateSettingsUseCase.updateSmsAlerts(event.enabled)
                }
            }
            SettingsEvent.OnPrivacyPolicyClicked -> {
                sendEffect(SettingsEffect.NavigateToPrivacyPolicy)
            }
            SettingsEvent.OnTermsClicked -> {}
            SettingsEvent.OnDeleteAccountClicked -> {}
            SettingsEvent.OnContactSupportClicked -> {}
        }
    }
}
