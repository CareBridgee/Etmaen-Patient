package com.carenest.domain.usecase.profile

import com.carenest.domain.model.profile.Profile
import com.carenest.domain.repository.ProfileRepository
import com.carenest.domain.repository.UserRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class GetDefaultProfileUseCase @Inject constructor(
    private val repository: ProfileRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<Profile> {
        val profileId = userRepository.observeCurrentUser().first()?.defaultProfileId
            ?: return Result.failure(IllegalStateException("The default profile is unavailable"))
        return repository.getProfile(profileId)
    }
}
