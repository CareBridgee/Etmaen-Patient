package com.carenest.data.source.remote.dto.aichat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AiChatRequestDto(
    @SerialName("profileId")
    val profileId: String,
    @SerialName("message")
    val message: String
)
