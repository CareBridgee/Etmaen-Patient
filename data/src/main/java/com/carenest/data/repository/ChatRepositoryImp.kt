package com.carenest.data.repository

import com.carenest.data.source.remote.datasource.ChatDataSource
import com.carenest.domain.model.chat.ChatMessage
import com.carenest.domain.model.chat.ChatSession
import com.carenest.domain.repository.ChatRepository
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val dataSource: ChatDataSource,
) : ChatRepository {

    override suspend fun getChatSession(requestId: String): Result<ChatSession> =
        runCatching { dataSource.fetchChatSession(requestId) }

    override suspend fun sendMessage(requestId: String, text: String): Result<ChatMessage> =
        runCatching { dataSource.sendMessage(requestId, text) }
}