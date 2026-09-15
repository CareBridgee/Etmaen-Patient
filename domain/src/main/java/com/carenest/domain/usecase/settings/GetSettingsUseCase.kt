package com.carenest.domain.usecase.settings

import com.carenest.domain.model.ThemeMode
import com.carenest.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

data class UserSettings(
    val languageCode: String,
    val themeMode: ThemeMode
)

class GetSettingsUseCase(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Flow<UserSettings> {
        return combine(
            settingsRepository.getLanguage(),
            settingsRepository.getThemeMode()
        ) { language, themeMode ->
            UserSettings(
                languageCode = language,
                themeMode = themeMode
            )
        }
    }
}
