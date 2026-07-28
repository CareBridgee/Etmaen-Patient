package com.carenest.domain.usecase.settings

import com.carenest.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

data class UserSettings(
    val languageCode: String,
    val isDarkMode: Boolean,
    val emailUpdatesEnabled: Boolean,
    val smsAlertsEnabled: Boolean
)

class GetSettingsUseCase(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Flow<UserSettings> {
        return combine(
            settingsRepository.getLanguage(),
            settingsRepository.getDarkMode(),
            settingsRepository.getEmailUpdates(),
            settingsRepository.getSmsAlerts()
        ) { language, isDark, emailUpdates, smsAlerts ->
            UserSettings(
                languageCode = language,
                isDarkMode = isDark,
                emailUpdatesEnabled = emailUpdates,
                smsAlertsEnabled = smsAlerts
            )
        }
    }
}
