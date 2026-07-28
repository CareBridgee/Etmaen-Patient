package com.carenest.data.repository

import com.carenest.data.di.IoDispatcher
import com.carenest.data.source.local.preferences.CarenestDatastore
import com.carenest.domain.repository.SettingsRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
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

    override suspend fun updateDarkMode(isDark: Boolean) {
        withContext(dispatcher) {
            datastore.setDarkMode(isDark)
        }
    }

    override fun getDarkMode(): Flow<Boolean> = datastore.isDarkMode

    override suspend fun updateEmailUpdates(enabled: Boolean) {
        withContext(dispatcher) {
            datastore.setEmailUpdates(enabled)
        }
    }

    override fun getEmailUpdates(): Flow<Boolean> = datastore.emailUpdates

    override suspend fun updateSmsAlerts(enabled: Boolean) {
        withContext(dispatcher) {
            datastore.setSmsAlerts(enabled)
        }
    }

    override fun getSmsAlerts(): Flow<Boolean> = datastore.smsAlerts
}