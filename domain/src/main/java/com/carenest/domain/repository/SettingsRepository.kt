package com.carenest.domain.repository

import kotlinx.coroutines.flow.Flow


interface SettingsRepository {

    suspend fun updateLoggedInStatus(status: Boolean)
    suspend fun getLoggedInStatus(): Flow<Boolean>


    suspend fun updateOnboardingStatus(status: Boolean)
    suspend fun getOnboardingStatus(): Flow<Boolean>

}