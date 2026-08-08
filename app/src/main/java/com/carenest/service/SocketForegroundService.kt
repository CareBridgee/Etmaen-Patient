package com.carenest.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.carenest.R
import com.carenest.domain.socket.SocketConnectionManager
import com.carenest.domain.socket.ConnectionState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

import android.content.pm.ServiceInfo

@AndroidEntryPoint
class SocketForegroundService : Service() {

    @Inject
    lateinit var socketConnectionManager: SocketConnectionManager

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    private val channelId = "carenest_socket_channel"
    private val notificationId = 1001

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = buildNotification("Connecting...")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(notificationId, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            startForeground(notificationId, notification)
        }
        
        socketConnectionManager.connect()
        
        serviceScope.launch {
            var lastMessage = ""
            socketConnectionManager.connectionState.collect { state ->
                val message = when (state) {
                    is ConnectionState.Connected -> "Connected"
                    is ConnectionState.Connecting -> "Connecting..."
                    is ConnectionState.Reconnecting -> "Reconnecting..."
                    is ConnectionState.Failed -> "Connection Failed"
                    is ConnectionState.Disconnected -> "Disconnected"
                }
                if (message != lastMessage) {
                    updateNotification(message)
                    lastMessage = message
                }
            }
        }
        
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        socketConnectionManager.disconnect()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun buildNotification(statusText: String): Notification {
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("CareNest Service")
            .setContentText(statusText)
            .setSmallIcon(android.R.drawable.ic_popup_sync) 
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification(statusText: String) {
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(notificationId, buildNotification(statusText))
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Socket Connection Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Keeps connection alive to receive real-time updates"
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}
