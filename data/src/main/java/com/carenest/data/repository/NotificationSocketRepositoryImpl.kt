package com.carenest.data.repository

import com.carenest.domain.repository.NotificationSocketRepository
import com.carenest.data.socket.SocketManagerImpl
import com.carenest.domain.socket.model.NotificationResponse
import com.carenest.data.socket.models.NotificationResponseDto
import com.carenest.data.socket.logger.SocketLogger
import com.carenest.data.socket.serialization.MessageSerializer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

class NotificationSocketRepositoryImpl @Inject constructor(
    private val socketManager: SocketManagerImpl,
    private val messageSerializer: MessageSerializer,
    private val logger: SocketLogger
) : NotificationSocketRepository {

    override fun observeNotifications(): Flow<NotificationResponse> {
        return socketManager.subscribe("/user/queue/notifications")
            .mapNotNull { raw ->
                val notification = messageSerializer.decodeFromString<NotificationResponseDto>(raw)?.toDomain()
                if (notification == null) {
                    logger.error("Dropping undecodable notification frame: $raw")
                }
                notification
            }
    }
}
