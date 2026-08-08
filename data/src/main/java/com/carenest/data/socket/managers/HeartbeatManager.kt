package com.carenest.data.socket.managers

import com.carenest.data.socket.logger.SocketLogger
import com.carenest.data.socket.stomp.StompClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.milliseconds

@Singleton
class HeartbeatManager @Inject constructor(
    private val stompClient: StompClient,
    private val logger: SocketLogger
) {
    private var heartbeatJob: Job? = null
    private var lastActivityTime = System.currentTimeMillis()

    // STOMP heartbeat interval: 10000ms (negotiated in CONNECT frame)
    private val heartbeatIntervalMs = 10000L
    // If no activity received from server within this window, consider connection dead
    private val serverTimeoutMs = 30000L

    fun notifyActivity() {
        lastActivityTime = System.currentTimeMillis()
    }

    fun start(scope: CoroutineScope) {
        stop()
        notifyActivity()
        heartbeatJob = scope.launch {
            while (isActive) {
                delay(heartbeatIntervalMs.milliseconds)

                if (System.currentTimeMillis() - lastActivityTime > serverTimeoutMs) {
                    logger.log("Heartbeat timeout: No activity for ${serverTimeoutMs}ms. Disconnecting.")
                    stompClient.disconnect()
                    break
                }

                logger.log("Sending STOMP heartbeat ping")
                stompClient.sendRaw("\n")
            }
        }
    }

    fun stop() {
        heartbeatJob?.cancel()
        heartbeatJob = null
    }
}
