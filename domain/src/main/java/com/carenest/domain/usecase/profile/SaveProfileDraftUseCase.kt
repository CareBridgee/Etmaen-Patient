package com.carenest.domain.usecase.profile

import com.carenest.domain.model.profile.ProfileLocalDraft
import com.carenest.domain.repository.ProfileRepository
import javax.inject.Inject

class SaveProfileDraftUseCase @Inject constructor(private val repository: ProfileRepository) {
    suspend operator fun invoke(userId: String, draft: ProfileLocalDraft): Result<Unit> =
        repository.saveLocalDraft(userId, draft)
}
