package com.carenest.domain.usecase.user

import com.carenest.domain.model.home.User
import com.carenest.domain.model.user.AuthenticatedDestination
import com.carenest.domain.model.user.AuthenticatedDestinationResolver
import com.carenest.domain.repository.ProfileRepository
import javax.inject.Inject

class GetAuthenticatedDestinationUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(user: User): Result<AuthenticatedDestination> {
        if (!user.hasCompletePersonalInformation) {
            return Result.success(AuthenticatedDestinationResolver.resolve(user))
        }

        val profileId = user.defaultProfileId
            ?: return Result.success(AuthenticatedDestination.CompleteProfile)
        val profile = profileRepository.getProfile(profileId)
            .getOrElse { return Result.failure(it) }
        val contacts = profileRepository.getEmergencyContacts(profileId)
            .getOrElse { return Result.failure(it) }

        return Result.success(
            AuthenticatedDestinationResolver.resolve(
                user = user,
                profile = profile,
                hasEmergencyContact = contacts.isNotEmpty()
            )
        )
    }
}
