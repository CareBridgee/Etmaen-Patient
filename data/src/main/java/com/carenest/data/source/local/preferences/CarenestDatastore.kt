package com.carenest.data.source.local.preferences

import kotlinx.coroutines.flow.Flow


interface CarenestDatastore {

    val isOnBoardingDone: Flow<Boolean>
    val isLoggedIn: Flow<Boolean>
    val languageCode: Flow<String>
    val isDarkMode: Flow<Boolean>
    val emailUpdates: Flow<Boolean>
    val smsAlerts: Flow<Boolean>

    suspend fun setOnboardingDone(done: Boolean)
    suspend fun setLoggedIn(done: Boolean)
    suspend fun setLanguageCode(languageCode: String)
    suspend fun setDarkMode(isDark: Boolean)
    suspend fun setEmailUpdates(enabled: Boolean)
    suspend fun setSmsAlerts(enabled: Boolean)

    suspend fun clearAll()
}