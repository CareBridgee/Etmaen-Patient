package com.carenest.data.socket

import com.carenest.domain.socket.ConnectionState
import com.carenest.domain.socket.SocketConnectionManager
import com.carenest.domain.socket.SocketError
import com.carenest.data.socket.logger.SocketLogger
import com.carenest.data.socket.managers.ConnectionManager
import com.carenest.data.socket.managers.TopicRegistry
import com.carenest.data.socket.stomp.StompClient
import com.carenest.data.socket.stomp.StompFrame
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class SocketManagerImpl @Inject constructor(
    private val connectionManager: ConnectionManager,
    private val topicRegistry: TopicRegistry,
    private val stompClient: StompClient,
    private val logger: SocketLogger
) : SocketConnectionManager {

    override val connectionState: Flow<ConnectionState>
        get() = connectionManager.connectionState

    override val socketErrors: Flow<SocketError>
        get() = connectionManager.socketErrors

    override fun connect() {
        logger.log("Connect requested by client")
        connectionManager.connect()
    }

    override fun disconnect() {
        logger.log("Disconnect requested by client")
        connectionManager.disconnect()
    }

    /**
     * Subscribes to a STOMP topic and returns a flow of raw JSON strings.
     * Used internally by repository implementations only.
     */
    fun subscribe(topic: String): Flow<String> {
        logger.log("Subscribing to topic: $topic")
        return topicRegistry.subscribe(topic)
    }

    /**
     * Unsubscribes from a STOMP topic.
     * Used internally by repository implementations only.
     */
    fun unsubscribe(topic: String) {
        logger.log("Unsubscribing from topic: $topic")
        topicRegistry.unsubscribe(topic)
    }

    /**
     * Sends a STOMP SEND frame with an optional JSON payload.
     * Used internally by repository implementations only.
     */
    suspend fun send(destination: String, payload: String? = null) {
        val headers = mutableMapOf("destination" to destination)
        if (payload != null) {
            headers["content-type"] = "application/json"
        }

        val frame = StompFrame(
            command = StompFrame.SEND,
            headers = headers,
            body = payload
        )
        logger.log("Sending payload to $destination")
        stompClient.send(frame)
    }

    /**
     * Sends an empty STOMP SEND frame to a destination.
     * Used internally by repository implementations only.
     */
    suspend fun sendEmpty(destination: String) {
        val frame = StompFrame(
            command = StompFrame.SEND,
            headers = mapOf("destination" to destination)
        )
        logger.log("Sending empty payload to $destination")
        stompClient.send(frame)
    }

    /**
     * Checks if the socket is currently connected.
     * Used internally by repository implementations only.
     */
    fun isConnected(): Boolean {
        val currentState = connectionManager.connectionState.value
        return currentState is ConnectionState.Connected
    }
}
