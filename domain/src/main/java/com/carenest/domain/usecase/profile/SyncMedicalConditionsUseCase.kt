package com.carenest.domain.usecase.profile

import com.carenest.domain.repository.ProfileRepository
import com.carenest.domain.validation.ProfileValidator
import javax.inject.Inject

class SyncMedicalConditionsUseCase @Inject constructor(private val repository: ProfileRepository) {
    suspend operator fun invoke(
        profileId: String,
        selectedBackendIds: Set<String>,
        otherConditions: String
    ): Result<Set<String>> {
        runCatching { ProfileValidator.medicalConditions(otherConditions) }
            .getOrElse { return Result.failure(it) }

        val currentBackendIds = repository.getProfileMedicalConditions(profileId)
            .getOrElse { return Result.failure(it) }
            .mapTo(linkedSetOf()) { it.medicalConditionId }

        return repository.syncProfileMedicalConditions(
            profileId = profileId,
            originalBackendIds = currentBackendIds,
            selectedBackendIds = selectedBackendIds
        )
    }
}
