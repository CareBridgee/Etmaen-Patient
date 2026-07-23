package com.carenest.domain.usecase.profile

import com.carenest.domain.model.profile.BasicHealthUpdate
import com.carenest.domain.model.profile.Profile
import com.carenest.domain.repository.ProfileRepository
import javax.inject.Inject

class UpdateBasicHealthUseCase @Inject constructor(private val repository: ProfileRepository) {
    suspend operator fun invoke(profileId: String, update: BasicHealthUpdate): Result<Profile> =
        repository.updateBasicHealth(profileId, update)
}
