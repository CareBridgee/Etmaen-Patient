package com.carenest.data.repository

import com.carenest.domain.repository.NotificationSocketRepository
import com.carenest.data.socket.SocketManagerImpl
import com.carenest.domain.socket.model.NotificationResponse
import com.carenest.data.socket.models.NotificationResponseDto
import com.carenest.data.socket.serialization.MessageSerializer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

class NotificationSocketRepositoryImpl @Inject constructor(
    private val socketManager: SocketManagerImpl,
    private val messageSerializer: MessageSerializer
) : NotificationSocketRepository {

    override fun observeNotifications(): Flow<NotificationResponse> {
        return socketManager.subscribe("/user/queue/notifications")
            .mapNotNull { messageSerializer.decodeFromString<NotificationResponseDto>(it)?.toDomain() }
    }
}
