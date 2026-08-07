package com.carenest.data.socket.managers

import com.carenest.data.socket.logger.SocketLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.pow
import kotlin.time.Duration.Companion.milliseconds

@Singleton
class ReconnectManager @Inject constructor(
    private val connectionManager: dagger.Lazy<ConnectionManager>,
    private val logger: SocketLogger
) {
    private var reconnectJob: Job? = null
    
    private val maxRetries = 10
    private val baseDelayMs = 2000.0

    fun scheduleReconnect(scope: CoroutineScope, attempt: Int) {
        if (attempt >= maxRetries) {
            logger.log("Max reconnect attempts reached ($maxRetries). Giving up.")
            return
        }

        reconnectJob?.cancel()
        reconnectJob = scope.launch {
            val delayMs = (baseDelayMs * 2.0.pow(attempt.toDouble())).toLong()
            logger.log("Scheduling reconnect attempt ${attempt + 1} in ${delayMs}ms")

            connectionManager.get().notifyReconnecting(attempt + 1, delayMs)
            
            delay(delayMs.milliseconds)
            
            logger.log("Executing reconnect attempt ${attempt + 1}")
            connectionManager.get().connect(isReconnect = true, attempt = attempt + 1)
        }
    }

    fun cancel() {
        reconnectJob?.cancel()
        reconnectJob = null
    }
}
