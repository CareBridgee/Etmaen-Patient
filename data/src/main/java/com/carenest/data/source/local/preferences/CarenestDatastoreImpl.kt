package com.carenest.data.source.local.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.carenest.data.di.IoDispatcher
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class CarenestDatastoreImpl @Inject constructor (
    private val dataStore: DataStore<Preferences>,
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CarenestDatastore {

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


    override suspend fun setOnboardingDone(done: Boolean) {
        withContext(coroutineDispatcher) {
            dataStore.edit {
                it[PreferenceKeys.IS_ONBOARDING_DONE] = done
            }
        }
    }

    override suspend fun setLoggedIn(done: Boolean) {
        withContext(coroutineDispatcher){
            dataStore.edit {
                it[PreferenceKeys.IS_LOGGED_IN] = done
            }
        }
    }

    override suspend fun clearAll() {
        withContext(coroutineDispatcher){
            dataStore.edit { it.clear() }
        }
    }


}

