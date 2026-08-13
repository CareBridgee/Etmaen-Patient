package com.carenest.domain.usecase.aichat

import com.carenest.domain.model.aichat.AiChatMessageResult
import com.carenest.domain.repository.AiChatRepository
import javax.inject.Inject

class SendAiChatMessageUseCase @Inject constructor(
    private val repository: AiChatRepository
) {
    suspend operator fun invoke(profileId: String, message: String): Result<AiChatMessageResult> =
        repository.sendChatMessage(profileId = profileId, message = message)
}
