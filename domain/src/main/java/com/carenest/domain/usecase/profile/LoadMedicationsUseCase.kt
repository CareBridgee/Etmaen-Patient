package com.carenest.domain.usecase.profile

import com.carenest.domain.model.profile.MedicationsData
import com.carenest.domain.repository.ProfileRepository
import javax.inject.Inject

class LoadMedicationsUseCase @Inject constructor(private val repository: ProfileRepository) {
    suspend operator fun invoke(profileId: String): Result<MedicationsData> {
        val catalog = repository.getMedicationCatalog().getOrElse { return Result.failure(it) }
        val saved = repository.getProfileMedications(profileId).getOrElse { return Result.failure(it) }
        return Result.success(MedicationsData(catalog, saved))
    }
}
