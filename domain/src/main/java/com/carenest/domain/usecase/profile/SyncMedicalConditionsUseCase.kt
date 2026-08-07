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
        val customName = runCatching { ProfileValidator.medicalConditions(otherConditions) }
            .getOrElse { return Result.failure(it) }

        val catalogIds = repository.getMedicalConditionCatalog()
            .getOrElse { return Result.failure(it) }
            .mapTo(hashSetOf()) { it.id }
        val current = repository.getProfileMedicalConditions(profileId)
            .getOrElse { return Result.failure(it) }
        val matchingCustomIds = if (customName.isBlank()) emptySet() else current
            .filter {
                it.medicalConditionId !in catalogIds &&
                    it.conditionName.equals(customName, ignoreCase = true)
            }
            .mapTo(linkedSetOf()) { it.medicalConditionId }
        val customIds = when {
            customName.isBlank() || matchingCustomIds.isNotEmpty() -> matchingCustomIds
            else -> setOf(
                repository.addCustomMedicalCondition(profileId, customName)
                    .getOrElse { return Result.failure(it) }
                    .medicalConditionId
            )
        }

        return repository.syncProfileMedicalConditions(
            profileId = profileId,
            originalBackendIds = current.mapTo(linkedSetOf()) { it.medicalConditionId },
            selectedBackendIds = selectedBackendIds + customIds
        )
    }
}
