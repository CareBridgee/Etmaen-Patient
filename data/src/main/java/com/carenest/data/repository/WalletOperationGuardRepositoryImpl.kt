package com.carenest.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.carenest.data.di.IoDispatcher
import com.carenest.domain.repository.WalletOperationGuardRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

@Singleton
class WalletOperationGuardRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    @param:IoDispatcher private val dispatcher: CoroutineDispatcher,
) : WalletOperationGuardRepository {
    override suspend fun isTopUpProcessed(transactionId: String): Boolean =
        contains(ProcessedTopUpsKey, transactionId)

    override suspend fun markTopUpProcessed(transactionId: String) {
        add(ProcessedTopUpsKey, transactionId)
    }

    override suspend fun isDeductionProcessed(serviceRequestId: String): Boolean =
        contains(ProcessedDeductionsKey, serviceRequestId)

    override suspend fun markDeductionProcessed(serviceRequestId: String) {
        add(ProcessedDeductionsKey, serviceRequestId)
    }

    private suspend fun contains(key: Preferences.Key<Set<String>>, value: String): Boolean =
        withContext(dispatcher) {
            dataStore.data.first()[key].orEmpty().contains(value)
        }

    private suspend fun add(key: Preferences.Key<Set<String>>, value: String) {
        withContext(dispatcher) {
            dataStore.edit { preferences ->
                preferences[key] = preferences[key].orEmpty() + value
            }
        }
    }

    private companion object {
        val ProcessedTopUpsKey = stringSetPreferencesKey("WALLET_PROCESSED_TOP_UPS_V1")
        val ProcessedDeductionsKey = stringSetPreferencesKey("WALLET_PROCESSED_DEDUCTIONS_V1")
    }
}
