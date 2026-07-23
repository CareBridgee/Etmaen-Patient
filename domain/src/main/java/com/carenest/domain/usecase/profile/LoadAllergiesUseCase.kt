package com.carenest.domain.usecase.profile

import com.carenest.domain.model.profile.AllergiesData
import com.carenest.domain.repository.ProfileRepository
import javax.inject.Inject

class LoadAllergiesUseCase @Inject constructor(private val repository: ProfileRepository) {
    suspend operator fun invoke(profileId: String): Result<AllergiesData> {
        val catalog = repository.getAllergyCatalog().getOrElse { return Result.failure(it) }
        val saved = repository.getProfileAllergies(profileId).getOrElse { return Result.failure(it) }
        return Result.success(AllergiesData(catalog, saved))
    }
}
