package com.carenest.domain.socket.model

data class ChatMessageResponse(
    val id: String,
    val serviceRequestId: String,
    val senderUserId: String,
    val senderName: String,
    val senderPhone: String,
    val content: String,
    val createdAt: String
)