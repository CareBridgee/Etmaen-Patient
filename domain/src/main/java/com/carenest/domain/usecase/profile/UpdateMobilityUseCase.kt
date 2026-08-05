package com.carenest.domain.usecase.profile

import com.carenest.domain.model.profile.MobilityStatus
import com.carenest.domain.model.profile.Profile
import com.carenest.domain.repository.ProfileRepository
import com.carenest.domain.validation.ProfileValidator
import javax.inject.Inject

class UpdateMobilityUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(
        profileId: String,
        status: MobilityStatus?,
        notes: String
    ): Result<Profile> {
        val update = runCatching { ProfileValidator.mobility(status, notes) }
            .getOrElse { return Result.failure(it) }
        return repository.updateMobility(profileId, update.status.name, update.notes)
    }
}
