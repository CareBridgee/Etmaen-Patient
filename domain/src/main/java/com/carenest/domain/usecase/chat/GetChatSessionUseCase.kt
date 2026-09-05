package com.carenest.domain.usecase.chat


import com.carenest.domain.model.ChatSession
import com.carenest.domain.repository.ChatRepository
import javax.inject.Inject

class GetChatSessionUseCase @Inject constructor(
    private val repository: ChatRepository,
) {
    suspend operator fun invoke(requestId: String): Result<ChatSession> =
        repository.getChatSession(requestId)
}