package com.carenest.domain.repository

import com.carenest.domain.model.settings.ThemeMode
import kotlinx.coroutines.flow.Flow


interface SettingsRepository {

    suspend fun updateLoggedInStatus(status: Boolean)
    suspend fun getLoggedInStatus(): Flow<Boolean>

    suspend fun updateOnboardingStatus(status: Boolean)
    suspend fun getOnboardingStatus(): Flow<Boolean>

    suspend fun updateLanguage(languageCode: String)
    fun getLanguage(): Flow<String>

    suspend fun updateThemeMode(themeMode: ThemeMode)
    fun getThemeMode(): Flow<ThemeMode>
}
