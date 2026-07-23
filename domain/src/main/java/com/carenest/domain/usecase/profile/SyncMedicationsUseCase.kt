package com.carenest.domain.usecase.profile

import com.carenest.domain.repository.ProfileRepository
import com.carenest.domain.model.profile.LocalMedicationEntry
import javax.inject.Inject

class SyncMedicationsUseCase @Inject constructor(private val repository: ProfileRepository) {
    suspend operator fun invoke(
        profileId: String,
        originalBackendIds: Set<String>,
        entries: List<LocalMedicationEntry>
    ): Result<List<LocalMedicationEntry>> = repository.syncProfileMedications(
        profileId,
        originalBackendIds,
        entries
    )
}
