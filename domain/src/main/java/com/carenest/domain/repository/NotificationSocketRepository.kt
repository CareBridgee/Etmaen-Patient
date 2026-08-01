package com.carenest.domain.repository

import com.carenest.domain.socket.model.NotificationResponse
import kotlinx.coroutines.flow.Flow

interface NotificationSocketRepository {
    fun observeNotifications(): Flow<NotificationResponse>
}
