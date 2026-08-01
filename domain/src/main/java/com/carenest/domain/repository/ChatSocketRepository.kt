package com.carenest.domain.repository

import com.carenest.domain.socket.model.ChatMessageResponse
import kotlinx.coroutines.flow.Flow

interface ChatSocketRepository {
    fun observeChat(reservationId: String): Flow<ChatMessageResponse>
    suspend fun sendMessage(reservationId: String, content: String)
}
