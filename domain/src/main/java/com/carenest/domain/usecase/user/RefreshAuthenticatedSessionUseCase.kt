package com.carenest.domain.usecase.user

import com.carenest.domain.model.user.AuthenticatedDestination
import com.carenest.domain.repository.UserRepository
import javax.inject.Inject

class RefreshAuthenticatedSessionUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val getDestination: GetAuthenticatedDestinationUseCase
) {
    suspend operator fun invoke(): Result<AuthenticatedDestination> =
        userRepository.refreshCurrentUser().fold(
            onSuccess = { getDestination(it) },
            onFailure = { Result.failure(it) }
        )
}
