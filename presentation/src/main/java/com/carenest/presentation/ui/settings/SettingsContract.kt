package com.carenest.presentation.ui.settings

import com.carenest.domain.model.settings.ThemeMode

data class SettingsState(
    val patientName: String = "",
    val languageCode: String = "en",
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val isLanguagePickerDialogVisible: Boolean = false,
    val isThemePickerDialogVisible: Boolean = false,
    val isLoading: Boolean = false
)

sealed interface SettingsEvent {
    data object OnBackClicked : SettingsEvent
    data object OnLanguageClicked : SettingsEvent
    data class OnLanguageSelected(val languageCode: String) : SettingsEvent
    data object OnDismissLanguagePicker : SettingsEvent
    data object OnThemeClicked : SettingsEvent
    data class OnThemeSelected(val themeMode: ThemeMode) : SettingsEvent
    data object OnDismissThemePicker : SettingsEvent
    data object OnTermsClicked : SettingsEvent
    data object OnDeleteAccountClicked : SettingsEvent
    data object OnContactSupportClicked : SettingsEvent
}

sealed interface SettingsEffect {
    data object NavigateBack : SettingsEffect
    data class ShowMessage(val message: SettingsMessage) : SettingsEffect
}

enum class SettingsMessage {
    SaveFailed,
    TermsUnavailable,
    DeleteAccountUnavailable,
    ContactSupportUnavailable
}
