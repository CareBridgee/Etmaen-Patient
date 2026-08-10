package com.carenest.data.socket.stomp

import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.header
import io.ktor.client.request.url
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

sealed interface StompClientEvent {
    data object Opened : StompClientEvent
    data class Message(val frame: StompFrame) : StompClientEvent
    data object Closed : StompClientEvent
    data class Failed(val cause: Throwable) : StompClientEvent
}

@Singleton
class StompClient @Inject constructor(
    private val httpClient: HttpClient
) {
    private var session: DefaultClientWebSocketSession? = null
    private var clientJob: Job? = null
    private val clientScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _events = MutableSharedFlow<StompClientEvent>(
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val events: SharedFlow<StompClientEvent> = _events.asSharedFlow()

    fun connect(urlString: String, token: String) {
        disconnect()

        clientJob = clientScope.launch {
            try {
                val wsSession = httpClient.webSocketSession {
                    url(urlString)
                    header("Authorization", "Bearer $token")
                    header("accept-version", "1.2,1.1,1.0")
                    header("heart-beat", "10000,10000")
                }
                session = wsSession

                // Send STOMP CONNECT frame immediately upon opening
                val connectFrame = StompFrame(
                    command = StompFrame.CONNECT,
                    headers = mapOf(
                        "Authorization" to "Bearer $token",
                        "accept-version" to "1.2,1.1,1.0",
                        "heart-beat" to "10000,10000"
                    )
                )
                wsSession.send(Frame.Text(StompFrameParser.serialize(connectFrame)))

                for (frame in wsSession.incoming) {
                    if (frame is Frame.Text) {
                        val text = frame.readText()
                        val stompFrame = StompFrameParser.parse(text)
                        if (stompFrame != null) {
                            _events.tryEmit(StompClientEvent.Message(stompFrame))
                            if (stompFrame.command == StompFrame.CONNECTED) {
                                _events.tryEmit(StompClientEvent.Opened)
                            } else if (stompFrame.command == StompFrame.ERROR) {
                                // Terminal ERROR frame — server closes connection after sending it
                                _events.tryEmit(
                                    StompClientEvent.Failed(
                                        Exception(stompFrame.headers["message"] ?: "STOMP Error")
                                    )
                                )
                            }
                        }
                    }
                }
                _events.tryEmit(StompClientEvent.Closed)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _events.tryEmit(StompClientEvent.Failed(e))
            } finally {
                session = null
            }
        }
    }

    suspend fun send(frame: StompFrame): Boolean {
        val currentSession = session ?: return false
        val text = StompFrameParser.serialize(frame)
        return try {
            currentSession.send(Frame.Text(text))
            true
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            false
        }
    }

    suspend fun sendRaw(text: String): Boolean {
        val currentSession = session ?: return false
        return try {
            currentSession.send(Frame.Text(text))
            true
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            false
        }
    }

    fun disconnect() {
        val currentSession = session
        session = null
        clientJob?.cancel()
        clientJob = null
        if (currentSession != null) {
            clientScope.launch {
                try {
                    currentSession.close(CloseReason(CloseReason.Codes.NORMAL, "Normal closure"))
                } catch (ignored: Exception) {
                }
            }
        }
    }
}
