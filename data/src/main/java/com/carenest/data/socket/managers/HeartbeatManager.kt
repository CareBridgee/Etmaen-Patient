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

    // STOMP protocol-level heartbeat interval: 10000ms (negotiated in CONNECT frame)
    private val protocolHeartbeatIntervalMs = 10000L
    // If no activity received from server within this window, consider connection dead.
    // Must be > 90s stale prune window on server side to avoid false disconnects.
    private val serverTimeoutMs = 60000L

    private var appHeartbeatTickCounter = 0

    fun notifyActivity() {
        lastActivityTime = System.currentTimeMillis()
    }

    fun start(scope: CoroutineScope) {
        stop()
        notifyActivity()
        appHeartbeatTickCounter = 0
        heartbeatJob = scope.launch {
            while (isActive) {
                delay(protocolHeartbeatIntervalMs.milliseconds)

                if (System.currentTimeMillis() - lastActivityTime > serverTimeoutMs) {
                    logger.log("Heartbeat timeout: No activity for ${serverTimeoutMs}ms. Disconnecting.")
                    stompClient.disconnect()
                    break
                }

                // Send STOMP protocol-level heartbeat (newline) every tick
                logger.log("Sending STOMP protocol heartbeat ping")
                stompClient.sendRaw("\n")
            }
        }
    }

    fun stop() {
        heartbeatJob?.cancel()
        heartbeatJob = null
    }
}
