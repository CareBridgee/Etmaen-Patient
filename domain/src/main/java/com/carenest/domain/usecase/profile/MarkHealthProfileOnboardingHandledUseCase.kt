package com.carenest.domain.usecase.profile

import com.carenest.domain.repository.ProfileRepository
import javax.inject.Inject

class MarkHealthProfileOnboardingHandledUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(userId: String): Result<Unit> =
        repository.markHealthProfileOnboardingHandled(userId)
}
