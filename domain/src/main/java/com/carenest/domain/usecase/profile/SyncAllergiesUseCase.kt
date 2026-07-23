package com.carenest.domain.usecase.profile

import com.carenest.domain.repository.ProfileRepository
import com.carenest.domain.validation.ProfileValidator
import javax.inject.Inject

class SyncAllergiesUseCase @Inject constructor(private val repository: ProfileRepository) {
    suspend operator fun invoke(
        profileId: String,
        selectedBackendIds: Set<String>,
        hasNoKnownAllergies: Boolean,
        otherAllergies: String
    ): Result<Set<String>> {
        val validatedIds = runCatching {
            ProfileValidator.allergies(
                hasNoKnownAllergies,
                selectedBackendIds,
                otherAllergies
            )
        }.getOrElse { return Result.failure(it) }

        val currentBackendIds = repository.getProfileAllergies(profileId)
            .getOrElse { return Result.failure(it) }
            .mapTo(linkedSetOf()) { it.allergyId }

        return repository.syncProfileAllergies(
            profileId = profileId,
            originalBackendIds = currentBackendIds,
            selectedBackendIds = validatedIds
        )
    }
}
