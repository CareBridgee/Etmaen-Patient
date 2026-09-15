package com.carenest.domain.repository


import com.carenest.domain.model.ChatMessage
import com.carenest.domain.model.ChatSession

interface ChatRepository {
    suspend fun getChatSession(requestId: String): Result<ChatSession>
    suspend fun sendMessage(requestId: String, text: String): Result<ChatMessage>
}