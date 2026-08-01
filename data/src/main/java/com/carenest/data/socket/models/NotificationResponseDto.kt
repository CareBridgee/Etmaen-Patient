package com.carenest.data.socket.models

import com.carenest.domain.socket.model.NotificationResponse
import kotlinx.serialization.Serializable

@Serializable
data class NotificationResponseDto(
    val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val type: String,
    val isRead: Boolean,
    val relatedEntityType: String?,
    val relatedEntityId: String?,
    val createdAt: String,
    val updatedAt: String
) {
    fun toDomain() = NotificationResponse(
        id, userId, title, message, type, isRead, relatedEntityType, relatedEntityId, createdAt, updatedAt
    )
}
