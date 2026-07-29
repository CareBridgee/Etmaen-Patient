package com.carenest.data.source.local.preferences

import kotlinx.coroutines.flow.Flow


interface CarenestDatastore {

    val isOnBoardingDone: Flow<Boolean>
    val isLoggedIn: Flow<Boolean>
    val authTokens: Flow<AuthTokens?>

    val languageCode: Flow<String>
    val isDarkMode: Flow<Boolean>
    val emailUpdates: Flow<Boolean>
    val smsAlerts: Flow<Boolean>
    val userId : Flow<String>

    suspend fun setOnboardingDone(done: Boolean)
    suspend fun setLoggedIn(done: Boolean)
    suspend fun saveAuthTokens(accessToken: String, refreshToken: String)
    suspend fun clearAuthTokens()
    suspend fun setLanguageCode(languageCode: String)
    suspend fun setDarkMode(isDark: Boolean)
    suspend fun setEmailUpdates(enabled: Boolean)
    suspend fun setSmsAlerts(enabled: Boolean)
    suspend fun setUserId(id: String)

    suspend fun clearAll()

}

data class AuthTokens(
    val accessToken: String,
    val refreshToken: String
)
