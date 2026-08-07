package com.carenest.domain.usecase.profile

import com.carenest.domain.model.profile.Profile
import com.carenest.domain.repository.ProfileRepository
import javax.inject.Inject

class GetProfilesUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(): Result<List<Profile>> = repository.getProfiles()
}
