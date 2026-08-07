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

        val customName = if (hasNoKnownAllergies) "" else otherAllergies.trim()
        val catalogIds = repository.getAllergyCatalog()
            .getOrElse { return Result.failure(it) }
            .mapTo(hashSetOf()) { it.id }
        val current = repository.getProfileAllergies(profileId)
            .getOrElse { return Result.failure(it) }
        val matchingCustomIds = if (customName.isBlank()) emptySet() else current
            .filter {
                it.allergyId !in catalogIds &&
                    it.allergyName.equals(customName, ignoreCase = true)
            }
            .mapTo(linkedSetOf()) { it.allergyId }
        val customIds = when {
            customName.isBlank() || matchingCustomIds.isNotEmpty() -> matchingCustomIds
            else -> setOf(
                repository.addCustomAllergy(profileId, customName)
                    .getOrElse { return Result.failure(it) }
                    .allergyId
            )
        }

        return repository.syncProfileAllergies(
            profileId = profileId,
            originalBackendIds = current.mapTo(linkedSetOf()) { it.allergyId },
            selectedBackendIds = validatedIds + customIds
        )
    }
}
