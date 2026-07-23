package com.carenest.data.source.local.profile

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.carenest.domain.model.profile.LocalMedicationEntry
import com.carenest.domain.model.profile.ProfileLocalDraft
import com.carenest.domain.model.profile.SyncState
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Singleton
class LocalProfileDraftDataSourceImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val json: Json
) : LocalProfileDraftDataSource {

    override suspend fun load(userId: String): ProfileLocalDraft {
        val raw = dataStore.data.map { it[draftKey(userId)] }.first() ?: return ProfileLocalDraft()
        return runCatching { json.decodeFromString<ProfileLocalDraftDto>(raw).toDomain() }
            .getOrDefault(ProfileLocalDraft())
    }

    override suspend fun save(userId: String, draft: ProfileLocalDraft) {
        dataStore.edit { preferences ->
            preferences[draftKey(userId)] = json.encodeToString(ProfileLocalDraftDto.fromDomain(draft))
        }
    }

    override suspend fun markOnboardingHandled(userId: String) {
        dataStore.edit { preferences ->
            preferences[booleanPreferencesKey("healthProfileOnboardingHandled_$userId")] = true
        }
    }

    private fun draftKey(userId: String) = stringPreferencesKey("healthProfileDraft_$userId")
}

@Serializable
private data class ProfileLocalDraftDto(
    val selectedConditionKeys: Set<String> = emptySet(),
    val otherConditions: String = "",
    val selectedAllergyKeys: Set<String> = emptySet(),
    val otherAllergies: String = "",
    val noKnownAllergiesConfirmed: Boolean = false,
    val medications: List<LocalMedicationEntryDto> = emptyList(),
    val noCurrentMedicationsConfirmed: Boolean = false,
    val pendingMobilityStatus: String? = null,
    val pendingMobilityNotes: String? = null
) {
    fun toDomain() = ProfileLocalDraft(
        selectedConditionKeys,
        otherConditions,
        selectedAllergyKeys,
        otherAllergies,
        noKnownAllergiesConfirmed,
        medications.map(LocalMedicationEntryDto::toDomain),
        noCurrentMedicationsConfirmed,
        pendingMobilityStatus,
        pendingMobilityNotes
    )

    companion object {
        fun fromDomain(value: ProfileLocalDraft) = ProfileLocalDraftDto(
            value.selectedConditionKeys,
            value.otherConditions,
            value.selectedAllergyKeys,
            value.otherAllergies,
            value.noKnownAllergiesConfirmed,
            value.medications.map(LocalMedicationEntryDto::fromDomain),
            value.noCurrentMedicationsConfirmed,
            value.pendingMobilityStatus,
            value.pendingMobilityNotes
        )
    }
}

@Serializable
private data class LocalMedicationEntryDto(
    val localId: String,
    val backendMedicationId: String? = null,
    val name: String = "",
    val dosage: String = "",
    val frequency: String = "",
    val syncState: String = SyncState.LOCAL_ONLY.name
) {
    fun toDomain() = LocalMedicationEntry(
        localId,
        backendMedicationId,
        name,
        dosage,
        frequency,
        runCatching { SyncState.valueOf(syncState) }.getOrDefault(SyncState.LOCAL_ONLY)
    )

    companion object {
        fun fromDomain(value: LocalMedicationEntry) = LocalMedicationEntryDto(
            value.localId,
            value.backendMedicationId,
            value.name,
            value.dosage,
            value.frequency,
            value.syncState.name
        )
    }
}
