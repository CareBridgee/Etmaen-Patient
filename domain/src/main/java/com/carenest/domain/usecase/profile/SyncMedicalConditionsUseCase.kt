package com.carenest.domain.usecase.profile

import com.carenest.domain.repository.ProfileRepository
import javax.inject.Inject

class SyncMedicalConditionsUseCase @Inject constructor(private val repository: ProfileRepository) {
    suspend operator fun invoke(
        profileId: String,
        originalBackendIds: Set<String>,
        selectedLocalKeys: Set<String>
    ): Result<Set<String>> = repository.syncProfileMedicalConditions(
        profileId,
        originalBackendIds,
        selectedLocalKeys
    )
}
