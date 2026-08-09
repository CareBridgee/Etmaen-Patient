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

        val catalogById = repository.getMedicalConditionCatalog()
            .getOrElse { return Result.failure(it) }
            .associateBy { it.id }
        val selectedNames = selectedBackendIds.mapNotNull { catalogById[it]?.name } +
            listOfNotNull(customName.takeIf(String::isNotBlank))

        return repository.syncProfileMedicalConditionsByName(
            profileId = profileId,
            names = selectedNames
        ).map { selectedBackendIds }
    }
}
