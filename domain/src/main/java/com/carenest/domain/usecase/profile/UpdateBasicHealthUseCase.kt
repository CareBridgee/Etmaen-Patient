package com.carenest.domain.usecase.profile

import com.carenest.domain.model.profile.Profile
import com.carenest.domain.repository.ProfileRepository
import com.carenest.domain.validation.ProfileValidator
import javax.inject.Inject

class UpdateBasicHealthUseCase @Inject constructor(private val repository: ProfileRepository) {
    suspend operator fun invoke(
        profileId: String,
        height: String,
        weight: String,
        bloodType: String
    ): Result<Profile> {
        val update = runCatching {
            ProfileValidator.basicHealth(height, weight, bloodType)
        }.getOrElse { return Result.failure(it) }
        return repository.updateBasicHealth(profileId, update)
    }
}
