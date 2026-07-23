package com.carenest.domain.usecase.profile

import com.carenest.domain.model.profile.MedicalHistoryUpdate
import com.carenest.domain.model.profile.Profile
import com.carenest.domain.repository.ProfileRepository
import javax.inject.Inject

class UpdateMedicalHistoryUseCase @Inject constructor(private val repository: ProfileRepository) {
    suspend operator fun invoke(profileId: String, update: MedicalHistoryUpdate): Result<Profile> =
        repository.updateMedicalHistory(profileId, update)
}
