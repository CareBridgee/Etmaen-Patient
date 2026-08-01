package com.carenest.data.socket.models

import com.carenest.domain.socket.model.ChatMessageResponse
import kotlinx.serialization.Serializable

@Serializable
data class ChatMessageResponseDto(
    val id: String,
    val serviceRequestId: String,
    val senderUserId: String,
    val content: String,
    val createdAt: String
) {
    fun toDomain() = ChatMessageResponse(id, serviceRequestId, senderUserId, content, createdAt)
}
