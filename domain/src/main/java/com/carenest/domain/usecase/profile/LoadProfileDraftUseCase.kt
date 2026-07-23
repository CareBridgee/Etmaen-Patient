package com.carenest.domain.usecase.profile

import com.carenest.domain.model.profile.ProfileLocalDraft
import com.carenest.domain.repository.ProfileRepository
import javax.inject.Inject

class LoadProfileDraftUseCase @Inject constructor(private val repository: ProfileRepository) {
    suspend operator fun invoke(userId: String): Result<ProfileLocalDraft> =
        repository.loadLocalDraft(userId)
}
