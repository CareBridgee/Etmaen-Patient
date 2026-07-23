package com.carenest.domain.usecase.profile

import com.carenest.domain.model.profile.Profile
import com.carenest.domain.repository.ProfileRepository
import com.carenest.domain.validation.ProfileValidator
import javax.inject.Inject

class UpdateMedicalHistoryUseCase @Inject constructor(private val repository: ProfileRepository) {
    suspend operator fun invoke(
        profileId: String,
        previousSurgeries: String,
        previousHospitalizations: String
    ): Result<Profile> {
        val update = runCatching {
            ProfileValidator.medicalHistory(previousSurgeries, previousHospitalizations)
        }.getOrElse { return Result.failure(it) }
        return repository.updateMedicalHistory(profileId, update)
    }
}
