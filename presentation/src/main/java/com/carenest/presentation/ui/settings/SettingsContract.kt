package com.carenest.presentation.ui.settings

data class SettingsState(
    val patientName: String = "Patient Name",
    val languageCode: String = "en",
    val isDarkMode: Boolean = false,
    val emailUpdatesEnabled: Boolean = true,
    val smsAlertsEnabled: Boolean = false,
    val isLanguagePickerDialogVisible: Boolean = false,
    val isLoading: Boolean = false
)

sealed interface SettingsEvent {
    data object OnBackClicked : SettingsEvent
    data object OnLanguageClicked : SettingsEvent
    data class OnLanguageSelected(val languageCode: String) : SettingsEvent
    data object OnDismissLanguagePicker : SettingsEvent
    data class OnDarkModeToggled(val enabled: Boolean) : SettingsEvent
    data class OnEmailUpdatesToggled(val enabled: Boolean) : SettingsEvent
    data class OnSmsAlertsToggled(val enabled: Boolean) : SettingsEvent
    data object OnPrivacyPolicyClicked : SettingsEvent
    data object OnTermsClicked : SettingsEvent
    data object OnDeleteAccountClicked : SettingsEvent
    data object OnContactSupportClicked : SettingsEvent
}

sealed interface SettingsEffect {
    data object NavigateBack : SettingsEffect
    data class ShowToast(val message: String) : SettingsEffect
    data object NavigateToPrivacyPolicy : SettingsEffect
}
