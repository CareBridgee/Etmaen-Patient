package com.carenest.data.source.remote.dto.aichat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AiChatResponseDto(
    @SerialName("reply")
    val reply: String
)
