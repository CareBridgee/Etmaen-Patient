package com.carenest.domain.usecase.profile

import com.carenest.domain.model.profile.MedicationInput
import com.carenest.domain.repository.ProfileRepository
import com.carenest.domain.validation.ProfileValidator
import javax.inject.Inject

class SyncMedicationsUseCase @Inject constructor(private val repository: ProfileRepository) {
    suspend operator fun invoke(
        profileId: String,
        hasNoCurrentMedications: Boolean,
        entries: List<MedicationInput>
    ): Result<List<String>> {
        val validated = runCatching {
            ProfileValidator.medications(hasNoCurrentMedications, entries)
        }.getOrElse { return Result.failure(it) }
        return repository.syncProfileMedications(profileId, validated.map { it.name })
    }
}
