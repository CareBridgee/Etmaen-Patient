package com.carenest.data.repository

import com.carenest.data.di.IoDispatcher
import com.carenest.data.source.local.preferences.CarenestDatastore
import com.carenest.domain.model.settings.ThemeMode
import com.carenest.domain.repository.SettingsRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext


class SettingsRepositoryImpl @Inject constructor (
    private val datastore: CarenestDatastore,
    @param:IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO
): SettingsRepository {
    override suspend fun updateLoggedInStatus(status: Boolean) {
        withContext(dispatcher) {
            datastore.setLoggedIn(status)
        }
    }

    override suspend fun updateOnboardingStatus(status: Boolean) {
        withContext(dispatcher) {
            datastore.setOnboardingDone(status)
        }
    }

    override suspend fun getOnboardingStatus(): Flow<Boolean> = datastore.isOnBoardingDone

    override suspend fun getLoggedInStatus(): Flow<Boolean> = datastore.isLoggedIn

    override suspend fun updateLanguage(languageCode: String) {
        withContext(dispatcher) {
            datastore.setLanguageCode(languageCode)
        }
    }

    override fun getLanguage(): Flow<String> = datastore.languageCode

    override suspend fun updateThemeMode(themeMode: ThemeMode) {
        withContext(dispatcher) {
            datastore.setThemeMode(themeMode.name)
        }
    }

    override fun getThemeMode(): Flow<ThemeMode> = datastore.themeMode.map(ThemeMode::fromStoredValue)
}
