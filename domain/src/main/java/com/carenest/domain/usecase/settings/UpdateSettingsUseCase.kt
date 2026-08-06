package com.carenest.domain.usecase.settings

import com.carenest.domain.model.settings.ThemeMode
import com.carenest.domain.repository.SettingsRepository

class UpdateSettingsUseCase(
    private val settingsRepository: SettingsRepository
) {
    suspend fun updateLanguage(languageCode: String) {
        settingsRepository.updateLanguage(languageCode)
    }

    suspend fun updateThemeMode(themeMode: ThemeMode) {
        settingsRepository.updateThemeMode(themeMode)
    }
}
