package com.carenest.data.socket.models

import com.carenest.domain.socket.model.NotificationResponse
import com.carenest.data.socket.serialization.FlexibleDateStringSerializer
import kotlinx.serialization.Serializable

@Serializable
data class NotificationResponseDto(
    val id: String? = null,
    val userId: String? = null,
    val title: String? = null,
    val message: String? = null,
    val type: String? = null,
    val isRead: Boolean? = null,
    val relatedEntityType: String? = null,
    val relatedEntityId: String? = null,
    @Serializable(with = FlexibleDateStringSerializer::class) val createdAt: String? = null,
    @Serializable(with = FlexibleDateStringSerializer::class) val updatedAt: String? = null
) {
    fun toDomain() = NotificationResponse(
        id ?: "",
        userId ?: "",
        title ?: "",
        message ?: "",
        type ?: "SYSTEM",
        isRead ?: false,
        relatedEntityType,
        relatedEntityId,
        createdAt ?: "",
        updatedAt ?: ""
    )
}
