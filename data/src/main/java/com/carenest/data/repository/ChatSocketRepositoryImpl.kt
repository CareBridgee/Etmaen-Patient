package com.carenest.data.repository

import com.carenest.domain.repository.ChatSocketRepository
import com.carenest.data.socket.SocketManagerImpl
import com.carenest.domain.socket.model.ChatMessageResponse
import com.carenest.domain.socket.model.SendMessageRequest
import com.carenest.data.socket.models.ChatMessageResponseDto
import com.carenest.data.socket.models.toDto
import com.carenest.data.socket.serialization.MessageSerializer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

class ChatSocketRepositoryImpl @Inject constructor(
    private val socketManager: SocketManagerImpl,
    private val messageSerializer: MessageSerializer
) : ChatSocketRepository {

    override fun observeChat(reservationId: String): Flow<ChatMessageResponse> {
        return socketManager.subscribe("/topic/chat/$reservationId")
            .mapNotNull { messageSerializer.decodeFromString<ChatMessageResponseDto>(it)?.toDomain() }
    }

    override suspend fun sendMessage(reservationId: String, content: String) {
        val request = SendMessageRequest(content = content)
        val payload = messageSerializer.encodeToString(request.toDto())
        socketManager.send("/app/chat/$reservationId/send", payload)
    }
}
