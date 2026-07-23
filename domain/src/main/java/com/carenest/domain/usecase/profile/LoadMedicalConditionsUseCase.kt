package com.carenest.domain.usecase.profile

import com.carenest.domain.model.profile.MedicalConditionsData
import com.carenest.domain.repository.ProfileRepository
import javax.inject.Inject

class LoadMedicalConditionsUseCase @Inject constructor(private val repository: ProfileRepository) {
    suspend operator fun invoke(profileId: String): Result<MedicalConditionsData> {
        val catalog = repository.getMedicalConditionCatalog().getOrElse { return Result.failure(it) }
        val saved = repository.getProfileMedicalConditions(profileId).getOrElse { return Result.failure(it) }
        return Result.success(MedicalConditionsData(catalog, saved))
    }
}
