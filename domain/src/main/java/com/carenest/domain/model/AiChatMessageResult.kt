package com.carenest.domain.model

data class AiChatMessageResult(
    val reply: String,
    val serviceTypeId: String? = null,
    val serviceTypeName: String? = null,
    val serviceDescription: String? = null,
    val careDescription: String? = null,
    val isComplete: Boolean = false
)
