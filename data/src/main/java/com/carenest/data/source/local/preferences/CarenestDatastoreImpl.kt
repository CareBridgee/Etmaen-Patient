package com.carenest.data.source.local.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.carenest.data.di.qualifier.IoDispatcher
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class CarenestDatastoreImpl @Inject constructor (
    private val dataStore: DataStore<Preferences>,
    @param:IoDispatcher private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : CarenestDatastore {

    override val authTokens: Flow<AuthTokens?>
        get() = dataStore.data.map { preferences ->
            val accessToken = preferences[PreferenceKeys.ACCESS_TOKEN]
            val refreshToken = preferences[PreferenceKeys.REFRESH_TOKEN]
            if (accessToken.isNullOrBlank() || refreshToken.isNullOrBlank()) {
                null
            } else {
                AuthTokens(accessToken, refreshToken)
            }
        }

    override val isLoggedIn: Flow<Boolean>
        get() {
            return dataStore.data.map {
                it[PreferenceKeys.IS_LOGGED_IN] ?: false
            }
        }

    override val isOnBoardingDone: Flow<Boolean>
        get() {
            return dataStore.data.map {
                it[PreferenceKeys.IS_ONBOARDING_DONE] ?: false
            }
        }

    override val languageCode: Flow<String>
        get() = dataStore.data.map { it[PreferenceKeys.LANGUAGE_CODE] ?: "en" }

    override val themeMode: Flow<String>
        get() = dataStore.data.map { preferences ->
            preferences[PreferenceKeys.THEME_MODE] ?: when (
                preferences[PreferenceKeys.IS_DARK_MODE]
            ) {
                true -> "DARK"
                false -> "LIGHT"
                null -> "SYSTEM"
            }
        }

    override val userId: Flow<String>
        get() = dataStore.data.map { it[PreferenceKeys.USER_ID]?: "Not Found UserId" }

    override suspend fun setOnboardingDone(done: Boolean) {
        withContext(coroutineDispatcher) {
            dataStore.edit {
                it[PreferenceKeys.IS_ONBOARDING_DONE] = done
            }
        }
    }

    override suspend fun setLoggedIn(done: Boolean) {
        withContext(coroutineDispatcher) {
            dataStore.edit {
                it[PreferenceKeys.IS_LOGGED_IN] = done
            }
        }
    }

    override suspend fun setLanguageCode(languageCode: String) {
        withContext(coroutineDispatcher) {
            dataStore.edit {
                it[PreferenceKeys.LANGUAGE_CODE] = languageCode
            }
        }
    }

    override suspend fun setThemeMode(themeMode: String) {
        withContext(coroutineDispatcher) {
            dataStore.edit { preferences ->
                preferences[PreferenceKeys.THEME_MODE] = themeMode
                preferences.remove(PreferenceKeys.IS_DARK_MODE)
            }
        }
    }

    override suspend fun setUserId(id: String) {
        withContext(coroutineDispatcher) {
            dataStore.edit {
                it[PreferenceKeys.USER_ID] = id
            }
        }
    }

    override suspend fun saveAuthTokens(accessToken: String, refreshToken: String) {
        require(accessToken.isNotBlank()) { "Access token must not be blank" }
        require(refreshToken.isNotBlank()) { "Refresh token must not be blank" }
        withContext(coroutineDispatcher) {
            dataStore.edit { preferences ->
                preferences[PreferenceKeys.ACCESS_TOKEN] = accessToken
                preferences[PreferenceKeys.REFRESH_TOKEN] = refreshToken
            }
        }
    }

    override suspend fun clearAuthTokens() {
        withContext(coroutineDispatcher) {
            dataStore.edit { preferences ->
                preferences.remove(PreferenceKeys.ACCESS_TOKEN)
                preferences.remove(PreferenceKeys.REFRESH_TOKEN)
            }
        }
    }

    override suspend fun clearAll() {
        withContext(coroutineDispatcher) {
            dataStore.edit { it.clear() }
        }
    }

    override suspend fun clearUserId() {
        withContext(coroutineDispatcher) {
            dataStore.edit { preferences ->
                preferences.remove(PreferenceKeys.USER_ID)
            }
        }
    }


}

