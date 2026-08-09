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
        val catalogById = repository.getAllergyCatalog()
            .getOrElse { return Result.failure(it) }
            .associateBy { it.id }
        val selectedNames = validatedIds.mapNotNull { catalogById[it]?.name } +
            listOfNotNull(customName.takeIf(String::isNotBlank))

        return repository.syncProfileAllergiesByName(
            profileId = profileId,
            names = selectedNames
        ).map { validatedIds }
    }
}
