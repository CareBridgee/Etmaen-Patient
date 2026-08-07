package com.carenest.domain.socket.model

data class NotificationResponse(
    val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val type: String, // BOOKING, PAYMENT, SYSTEM, MESSAGE, REMINDER
    val isRead: Boolean,
    val relatedEntityType: String?,
    val relatedEntityId: String?,
    val createdAt: String,
    val updatedAt: String
)
