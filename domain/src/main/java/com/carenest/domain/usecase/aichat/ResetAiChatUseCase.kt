package com.carenest.domain.usecase.aichat

import com.carenest.domain.repository.AiChatRepository
import javax.inject.Inject

class ResetAiChatUseCase @Inject constructor(
    private val repository: AiChatRepository
) {
    suspend operator fun invoke(profileId: String): Result<Unit> =
        repository.resetChat(profileId = profileId)
}
