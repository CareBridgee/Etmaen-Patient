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
    @IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO
): SettingsRepository {
    override suspend fun updateLoggedInStatus(status: Boolean) {
        withContext(dispatcher){
            datastore.setLoggedIn(status)
        }
    }

    override suspend fun updateOnboardingStatus(status: Boolean) {
        withContext(dispatcher){
            datastore.setOnboardingDone(status)
        }
    }

    override suspend fun getOnboardingStatus(): Flow<Boolean> = datastore.isOnBoardingDone

    override suspend fun getLoggedInStatus(): Flow<Boolean> = datastore.isLoggedIn
}