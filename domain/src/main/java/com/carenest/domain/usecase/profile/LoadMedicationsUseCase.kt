package com.carenest.domain.usecase.profile

import com.carenest.domain.model.profile.ProfileMedication
import com.carenest.domain.repository.ProfileRepository
import javax.inject.Inject

class LoadMedicationsUseCase @Inject constructor(private val repository: ProfileRepository) {
    suspend operator fun invoke(profileId: String): Result<List<ProfileMedication>> =
        repository.getProfileMedications(profileId)
}
