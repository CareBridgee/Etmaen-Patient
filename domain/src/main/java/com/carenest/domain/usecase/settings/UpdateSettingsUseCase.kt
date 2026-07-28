package com.carenest.domain.usecase.settings

import com.carenest.domain.repository.SettingsRepository

class UpdateSettingsUseCase(
    private val settingsRepository: SettingsRepository
) {
    suspend fun updateLanguage(languageCode: String) {
        settingsRepository.updateLanguage(languageCode)
    }

    suspend fun updateDarkMode(isDark: Boolean) {
        settingsRepository.updateDarkMode(isDark)
    }

    suspend fun updateEmailUpdates(enabled: Boolean) {
        settingsRepository.updateEmailUpdates(enabled)
    }

    suspend fun updateSmsAlerts(enabled: Boolean) {
        settingsRepository.updateSmsAlerts(enabled)
    }
}
