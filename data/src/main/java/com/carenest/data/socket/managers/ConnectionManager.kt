package com.carenest.data.socket.managers

import com.carenest.data.BuildConfig
import com.carenest.data.socket.logger.SocketLogger
import com.carenest.data.socket.stomp.StompClient
import com.carenest.data.socket.stomp.StompClientEvent
import com.carenest.data.socket.stomp.StompFrame
import com.carenest.data.source.local.preferences.CarenestDatastore
import com.carenest.domain.repository.AuthRepository
import com.carenest.domain.socket.ConnectionState
import com.carenest.domain.socket.SocketError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConnectionManager @Inject constructor(
    private val stompClient: StompClient,
    private val datastore: CarenestDatastore,
    private val authRepository: AuthRepository,
    private val reconnectManager: ReconnectManager,
    private val heartbeatManager: HeartbeatManager,
    private val topicRegistry: TopicRegistry,
    private val logger: SocketLogger
) {
    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val _socketErrors = MutableSharedFlow<SocketError>(extraBufferCapacity = 64)
    val socketErrors: SharedFlow<SocketError> = _socketErrors.asSharedFlow()

    private var connectionScope: CoroutineScope? = null
    private var isIntentionallyDisconnected = true
    
    // Construct WebSocket URL from the REST base URL
    private val socketUrl = BuildConfig.base_url.replace("http", "ws").removeSuffix("/") + "/ws"

    fun connect(isReconnect: Boolean = false, attempt: Int = 0) {
        if (_connectionState.value is ConnectionState.Connecting && !isReconnect) {
            logger.log("Already connecting, ignoring request")
            return
        }

        if (!isReconnect) {
            isIntentionallyDisconnected = false
            reconnectManager.cancel()
            _connectionState.value = ConnectionState.Connecting
        }

        // Create a new scope for this connection lifecycle if needed
        if (connectionScope == null || !connectionScope!!.isActive) {
            connectionScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
            listenToStompEvents()
            topicRegistry.init(connectionScope!!)
        }

        connectionScope?.launch {
            try {
                logger.log("Refreshing tokens before connection...")
                val refreshResult = authRepository.refreshToken()
                
                if (refreshResult.isFailure) {
                    val error = refreshResult.exceptionOrNull()
                    logger.error("Token refresh failed", error)
                    _socketErrors.tryEmit(SocketError.AuthFailed("Token refresh failed: ${error?.message}"))
                    _connectionState.value = ConnectionState.Failed(error ?: Exception("Token refresh failed"))
                    // Don't auto-reconnect if it's an auth failure (user needs to log in)
                    return@launch
                }

                val authTokens = datastore.authTokens.first()
                val token = authTokens?.accessToken
                
                if (token.isNullOrBlank()) {
                    _socketErrors.tryEmit(SocketError.AuthFailed("No access token available"))
                    _connectionState.value = ConnectionState.Failed(Exception("No token"))
                    return@launch
                }
                
                logger.log("Initiating StompClient connection to $socketUrl")
                stompClient.connect(socketUrl, token)
                
            } catch (e: Exception) {
                logger.error("Failed to connect", e)
                _connectionState.value = ConnectionState.Failed(e)
                handleDrop(attempt)
            }
        }
    }

    fun disconnect() {
        isIntentionallyDisconnected = true
        reconnectManager.cancel()
        heartbeatManager.stop()
        stompClient.disconnect()
        connectionScope?.cancel()
        connectionScope = null
        _connectionState.value = ConnectionState.Disconnected
    }

    fun notifyReconnecting(attempt: Int, delayMs: Long) {
        _connectionState.value = ConnectionState.Reconnecting(attempt, delayMs)
    }

    private fun listenToStompEvents() {
        connectionScope?.launch {
            stompClient.events.collect { event ->
                when (event) {
                    is StompClientEvent.Opened -> {
                        logger.log("WebSocket Opened, wait for STOMP CONNECTED")
                    }
                    is StompClientEvent.Message -> {
                        val frame = event.frame
                        when (frame.command) {
                            StompFrame.CONNECTED -> {
                                logger.log("STOMP Connected successfully")
                                _connectionState.value = ConnectionState.Connected
                                reconnectManager.cancel()
                                heartbeatManager.start(connectionScope!!)
                            }
                            StompFrame.ERROR -> {
                                logger.error("STOMP Error: ${frame.headers["message"]}")
                                _socketErrors.tryEmit(
                                    SocketError.StompError(
                                        message = frame.headers["message"] ?: "Unknown error",
                                        body = frame.body
                                    )
                                )
                            }
                        }
                    }
                    is StompClientEvent.Closed -> {
                        logger.log("WebSocket Closed")
                        heartbeatManager.stop()
                        if (!isIntentionallyDisconnected) {
                            handleDrop(0)
                        }
                    }
                    is StompClientEvent.Failed -> {
                        logger.error("WebSocket Failed", event.cause)
                        heartbeatManager.stop()
                        _socketErrors.tryEmit(SocketError.NetworkError(event.cause))
                        _connectionState.value = ConnectionState.Failed(event.cause)
                        if (!isIntentionallyDisconnected) {
                            handleDrop(0)
                        }
                    }
                }
            }
        }
    }

    private fun handleDrop(attempt: Int) {
        if (connectionScope != null && connectionScope!!.isActive) {
            val nextAttempt = if (_connectionState.value is ConnectionState.Reconnecting) {
                (_connectionState.value as ConnectionState.Reconnecting).attempt
            } else attempt
            
            reconnectManager.scheduleReconnect(connectionScope!!, nextAttempt)
        }
    }
}
