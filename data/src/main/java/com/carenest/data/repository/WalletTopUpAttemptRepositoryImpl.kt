package com.carenest.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.carenest.data.di.IoDispatcher
import com.carenest.domain.model.payment.WalletTopUpAmount
import com.carenest.domain.model.payment.WalletTopUpAttempt
import com.carenest.domain.model.payment.WalletTopUpAttemptState
import com.carenest.domain.repository.WalletTopUpAttemptRepository
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Singleton
class WalletTopUpAttemptRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val json: Json,
    @param:IoDispatcher private val dispatcher: CoroutineDispatcher,
) : WalletTopUpAttemptRepository {
    override suspend fun getActiveAttempt(): WalletTopUpAttempt? {
        val attempt = readAttempt() ?: return null
        if (attempt.state in TerminalStates) return null
        if (attempt.isExpired()) {
            updateAttemptState(
                localAttemptId = attempt.localAttemptId,
                state = WalletTopUpAttemptState.Failed,
            )
            return null
        }
        return attempt
    }

    override suspend fun saveAttempt(attempt: WalletTopUpAttempt) {
        writeAttempt(attempt.withFreshTimestamps())
    }

    override suspend fun updateAttemptState(
        localAttemptId: String,
        state: WalletTopUpAttemptState,
        paymobTransactionId: String?,
        creditAddSucceeded: Boolean?,
    ) {
        val current = readAttempt() ?: return
        if (current.localAttemptId != localAttemptId) return

        writeAttempt(
            current.copy(
                state = state,
                paymobTransactionId = paymobTransactionId ?: current.paymobTransactionId,
                creditAddSucceeded = creditAddSucceeded ?: current.creditAddSucceeded,
                updatedAtEpochMillis = now(),
            ),
        )
    }

    override suspend fun findCreditAddPendingAttempt(): WalletTopUpAttempt? =
        readAttempt()?.takeIf {
            it.state == WalletTopUpAttemptState.CreditAddPending && !it.creditAddSucceeded
        }

    override suspend fun createAttempt(
        amount: WalletTopUpAmount,
        merchantReference: String,
    ): WalletTopUpAttempt {
        val timestamp = now()
        val attempt = WalletTopUpAttempt(
            localAttemptId = UUID.randomUUID().toString(),
            merchantReference = merchantReference,
            amount = amount,
            state = WalletTopUpAttemptState.Created,
            creditAddSucceeded = false,
            createdAtEpochMillis = timestamp,
            updatedAtEpochMillis = timestamp,
        )
        writeAttempt(attempt)
        return attempt
    }

    private suspend fun readAttempt(): WalletTopUpAttempt? =
        withContext(dispatcher) {
            val raw = dataStore.data.first()[ActiveTopUpAttemptKey] ?: return@withContext null
            runCatching {
                json.decodeFromString<StoredWalletTopUpAttempt>(raw).toDomain()
            }.getOrNull()
        }

    private suspend fun writeAttempt(attempt: WalletTopUpAttempt) {
        withContext(dispatcher) {
            dataStore.edit { preferences ->
                preferences[ActiveTopUpAttemptKey] = json.encodeToString(
                    StoredWalletTopUpAttempt.fromDomain(attempt),
                )
            }
        }
    }

    private fun WalletTopUpAttempt.isExpired(): Boolean {
        if (state == WalletTopUpAttemptState.CreditAddPending) return false
        val lastUpdate = updatedAtEpochMillis.takeIf { it > 0L } ?: return true
        return now() - lastUpdate > ActiveAttemptTimeoutMillis
    }

    private fun WalletTopUpAttempt.withFreshTimestamps(): WalletTopUpAttempt {
        val timestamp = now()
        return copy(
            createdAtEpochMillis = createdAtEpochMillis.takeIf { it > 0L } ?: timestamp,
            updatedAtEpochMillis = updatedAtEpochMillis.takeIf { it > 0L } ?: timestamp,
        )
    }

    private fun now(): Long = System.currentTimeMillis()

    private companion object {
        val ActiveTopUpAttemptKey = stringPreferencesKey("WALLET_ACTIVE_TOP_UP_ATTEMPT_V1")
        const val ActiveAttemptTimeoutMillis = 10 * 60 * 1000L
        val TerminalStates = setOf(
            WalletTopUpAttemptState.Cancelled,
            WalletTopUpAttemptState.Failed,
            WalletTopUpAttemptState.Pending,
            WalletTopUpAttemptState.Credited,
        )
    }
}

@Serializable
private data class StoredWalletTopUpAttempt(
    @SerialName("local_attempt_id") val localAttemptId: String,
    @SerialName("merchant_reference") val merchantReference: String,
    @SerialName("amount_egp") val amountEgp: String,
    @SerialName("amount_minor_units") val amountMinorUnits: Long,
    @SerialName("state") val state: WalletTopUpAttemptState,
    @SerialName("paymob_transaction_id") val paymobTransactionId: String? = null,
    @SerialName("credit_add_succeeded") val creditAddSucceeded: Boolean = false,
    @SerialName("created_at_epoch_millis") val createdAtEpochMillis: Long = 0L,
    @SerialName("updated_at_epoch_millis") val updatedAtEpochMillis: Long = 0L,
) {
    fun toDomain(): WalletTopUpAttempt {
        val amount = WalletTopUpAmount.parse(amountEgp).getOrThrow()
        return WalletTopUpAttempt(
            localAttemptId = localAttemptId,
            merchantReference = merchantReference,
            amount = amount,
            state = state,
            paymobTransactionId = paymobTransactionId,
            creditAddSucceeded = creditAddSucceeded,
            createdAtEpochMillis = createdAtEpochMillis,
            updatedAtEpochMillis = updatedAtEpochMillis,
        )
    }

    companion object {
        fun fromDomain(attempt: WalletTopUpAttempt): StoredWalletTopUpAttempt =
            StoredWalletTopUpAttempt(
                localAttemptId = attempt.localAttemptId,
                merchantReference = attempt.merchantReference,
                amountEgp = attempt.amount.egp,
                amountMinorUnits = attempt.amount.minorUnits,
                state = attempt.state,
                paymobTransactionId = attempt.paymobTransactionId,
                creditAddSucceeded = attempt.creditAddSucceeded,
                createdAtEpochMillis = attempt.createdAtEpochMillis,
                updatedAtEpochMillis = attempt.updatedAtEpochMillis,
            )
    }
}
