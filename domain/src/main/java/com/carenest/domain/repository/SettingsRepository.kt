package com.carenest.domain.repository

import kotlinx.coroutines.flow.Flow


interface SettingsRepository {

    suspend fun updateLoggedInStatus(status: Boolean)
    suspend fun getLoggedInStatus(): Flow<Boolean>

    suspend fun updateOnboardingStatus(status: Boolean)
    suspend fun getOnboardingStatus(): Flow<Boolean>

    suspend fun updateLanguage(languageCode: String)
    fun getLanguage(): Flow<String>

    suspend fun updateDarkMode(isDark: Boolean)
    fun getDarkMode(): Flow<Boolean>

    suspend fun updateEmailUpdates(enabled: Boolean)
    fun getEmailUpdates(): Flow<Boolean>

    suspend fun updateSmsAlerts(enabled: Boolean)
    fun getSmsAlerts(): Flow<Boolean>
}