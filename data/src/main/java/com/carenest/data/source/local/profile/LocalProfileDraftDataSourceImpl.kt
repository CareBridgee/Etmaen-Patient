package com.carenest.data.source.local.profile

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.carenest.domain.model.profile.LocalMedicationEntry
import com.carenest.domain.model.profile.ProfileLocalDraft
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
    val otherConditions: String = "",
    val otherAllergies: String = "",
    val noKnownAllergiesConfirmed: Boolean = false,
    val medications: List<LocalMedicationEntryDto> = emptyList(),
    val noCurrentMedicationsConfirmed: Boolean = false,
    val pendingMobilityStatus: String? = null,
    val pendingMobilityNotes: String? = null
) {
    fun toDomain() = ProfileLocalDraft(
        otherConditions = otherConditions,
        otherAllergies = otherAllergies,
        noKnownAllergiesConfirmed = noKnownAllergiesConfirmed,
        medications = medications.map(LocalMedicationEntryDto::toDomain),
        noCurrentMedicationsConfirmed = noCurrentMedicationsConfirmed,
        pendingMobilityStatus = pendingMobilityStatus,
        pendingMobilityNotes = pendingMobilityNotes
    )

    companion object {
        fun fromDomain(value: ProfileLocalDraft) = ProfileLocalDraftDto(
            otherConditions = value.otherConditions,
            otherAllergies = value.otherAllergies,
            noKnownAllergiesConfirmed = value.noKnownAllergiesConfirmed,
            medications = value.medications.map(LocalMedicationEntryDto::fromDomain),
            noCurrentMedicationsConfirmed = value.noCurrentMedicationsConfirmed,
            pendingMobilityStatus = value.pendingMobilityStatus,
            pendingMobilityNotes = value.pendingMobilityNotes
        )
    }
}

@Serializable
private data class LocalMedicationEntryDto(
    val localId: String,
    val name: String = ""
) {
    fun toDomain() = LocalMedicationEntry(localId = localId, name = name)

    companion object {
        fun fromDomain(value: LocalMedicationEntry) = LocalMedicationEntryDto(
            localId = value.localId,
            name = value.name
        )
    }
}
