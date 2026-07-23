package com.carenest.domain.usecase.profile

import com.carenest.domain.model.profile.MobilityUpdate
import com.carenest.domain.model.profile.Profile
import com.carenest.domain.repository.ProfileRepository
import javax.inject.Inject

class UpdateMobilityUseCase @Inject constructor(private val repository: ProfileRepository) {
    suspend operator fun invoke(profileId: String, update: MobilityUpdate): Result<Profile> =
        repository.updateMobility(profileId, update)
}
