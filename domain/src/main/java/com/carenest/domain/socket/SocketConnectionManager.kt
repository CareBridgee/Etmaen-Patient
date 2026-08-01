package com.carenest.domain.socket

import kotlinx.coroutines.flow.Flow

interface SocketConnectionManager {

    /**
     * A continuous stream of the connection state.
     */
    val connectionState: Flow<ConnectionState>

    /**
     * A stream of errors occurring on the socket.
     */
    val socketErrors: Flow<SocketError>

    /**
     * Connects to the WebSocket server using the current authentication token.
     * Starts the auto-reconnect logic.
     */
    fun connect()

    /**
     * Disconnects from the WebSocket server and stops auto-reconnecting.
     */
    fun disconnect()
}
