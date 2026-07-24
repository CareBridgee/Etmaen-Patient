package com.carenest.domain.model.chat

enum class ChatMessageType { INCOMING, OUTGOING, SYSTEM_TIP }

enum class MessageStatus { SENDING, SENT, DELIVERED, SEEN }

data class ChatMessage(
    val id: String,
    val type: ChatMessageType,
    val text: String,
    val sentAtEpochMillis: Long,
    val status: MessageStatus = MessageStatus.SENT,
)

data class ChatParticipant(
    val nurseId: String,
    val name: String,
    val photoUrl: String?,
    val isOnline: Boolean,
    val phoneNumber: String,
)

data class ChatSession(
    val participant: ChatParticipant,
    val messages: List<ChatMessage>,
)