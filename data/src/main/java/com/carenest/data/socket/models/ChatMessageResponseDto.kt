package com.carenest.data.socket.models

import com.carenest.domain.socket.model.ChatMessageResponse
import kotlinx.serialization.Serializable

@Serializable
data class ChatMessageResponseDto(
    val id: String? = null,
    val serviceRequestId: String? = null,
    val senderUserId: String? = null,
    val senderName: String? = null,
    val senderPhone: String? = null,
    val content: String? = null,
    val createdAt: String? = null
) {
    fun toDomain() = ChatMessageResponse(
        id = id ?: "",
        serviceRequestId = serviceRequestId ?: "",
        senderUserId = senderUserId ?: "",
        senderName = senderName ?: "",
        senderPhone = senderPhone ?: "",
        content = content ?: "",
        createdAt = createdAt ?: ""
    )
}
