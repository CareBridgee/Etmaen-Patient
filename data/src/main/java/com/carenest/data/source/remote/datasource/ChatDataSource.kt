package com.carenest.data.source.remote.datasource


import com.carenest.domain.model.ChatMessage
import com.carenest.domain.model.ChatSession

interface ChatDataSource {
    suspend fun fetchChatSession(requestId: String): ChatSession
    suspend fun sendMessage(requestId: String, text: String): ChatMessage
}