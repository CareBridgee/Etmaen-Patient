package com.carenest.domain.usecase.aichat

import com.carenest.domain.repository.AiChatRepository
import javax.inject.Inject

class SendAiChatMessageUseCase @Inject constructor(
    private val repository: AiChatRepository
) {
    suspend operator fun invoke(message: String): Result<String> =
        repository.sendChatMessage(message)
}
