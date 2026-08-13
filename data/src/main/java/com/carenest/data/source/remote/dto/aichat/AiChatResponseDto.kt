package com.carenest.data.source.remote.dto.aichat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AiChatDraftDto(
    @SerialName("serviceTypeId")
    val serviceTypeId: String? = null,
    @SerialName("serviceTypeName")
    val serviceTypeName: String? = null,
    @SerialName("serviceDescription")
    val serviceDescription: String? = null,
    @SerialName("careDescription")
    val careDescription: String? = null,
    @SerialName("complete")
    val complete: Boolean? = null
)

@Serializable
data class AiChatUrgencyDto(
    @SerialName("urgent")
    val urgent: Boolean? = null,
    @SerialName("level")
    val level: String? = null,
    @SerialName("advice")
    val advice: String? = null
)

@Serializable
data class AiChatResponseDto(
    @SerialName("messageType")
    val messageType: String? = null,
    @SerialName("reply")
    val reply: String,
    @SerialName("draft")
    val draft: AiChatDraftDto? = null,
    @SerialName("urgency")
    val urgency: AiChatUrgencyDto? = null
)
