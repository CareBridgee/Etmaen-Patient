package com.carenest.domain.repository

interface AiChatRepository {
    suspend fun sendChatMessage(message: String): Result<String>
}
