package com.carenest.domain.repository

import com.carenest.domain.model.AiChatMessageResult

interface AiChatRepository {
    suspend fun sendChatMessage(profileId: String, message: String): Result<AiChatMessageResult>
    fun getLastAiReport(): String?
    fun setLastAiReport(report: String?)
}
