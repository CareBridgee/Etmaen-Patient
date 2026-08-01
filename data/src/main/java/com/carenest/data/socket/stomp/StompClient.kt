package com.carenest.data.socket.stomp

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
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
    private val okHttpClient: OkHttpClient
) {
    private var webSocket: WebSocket? = null

    private val _events = MutableSharedFlow<StompClientEvent>(
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val events: SharedFlow<StompClientEvent> = _events.asSharedFlow()

    fun connect(url: String, token: String) {
        disconnect()

        val httpUrl = url.replace("wss", "https").replace("ws", "http").toHttpUrl()
        val origin = "${httpUrl.scheme}://${httpUrl.host}${if (httpUrl.port != HttpUrl.defaultPort(httpUrl.scheme)) ":${httpUrl.port}" else ""}"

        // Some servers expect the token as a query parameter in the handshake
        val encodedToken = java.net.URLEncoder.encode(token, "UTF-8")
        val socketUrl = if (url.contains("?")) {
            "$url&token=$encodedToken"
        } else {
            "$url?token=$encodedToken"
        }

        val request = Request.Builder()
            .url(socketUrl)
            .addHeader("Authorization", "Bearer $token")
            .addHeader("Origin", origin)
            .addHeader("User-Agent", "CareNest-Android")
            .addHeader("X-Requested-With", "XMLHttpRequest")
            .build()

        webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                // Send the STOMP CONNECT frame immediately upon opening
                val connectFrame = StompFrame(
                    command = StompFrame.CONNECT,
                    headers = mapOf(
                        "Authorization" to "Bearer $token",
                        "accept-version" to "1.2,1.1,1.0",
                        "heart-beat" to "10000,10000",
                        "host" to httpUrl.host
                    )
                )
                webSocket.send(StompFrameParser.serialize(connectFrame))
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                val frame = StompFrameParser.parse(text)
                if (frame != null) {
                    _events.tryEmit(StompClientEvent.Message(frame))
                    if (frame.command == StompFrame.CONNECTED) {
                        _events.tryEmit(StompClientEvent.Opened)
                    }
                }
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                _events.tryEmit(StompClientEvent.Closed)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                _events.tryEmit(StompClientEvent.Failed(t))
            }
        })
    }

    fun send(frame: StompFrame): Boolean {
        val socket = webSocket ?: return false
        val text = StompFrameParser.serialize(frame)
        return socket.send(text)
    }

    /**
     * Sends a raw text string directly over the WebSocket without STOMP frame serialization.
     * Used for STOMP protocol-level heartbeats (single newline character).
     */
    fun sendRaw(text: String): Boolean {
        val socket = webSocket ?: return false
        return socket.send(text)
    }

    fun disconnect() {
        val socket = webSocket ?: return
        webSocket = null
        // Only request close — the onClosed callback will emit the Closed event.
        socket.close(1000, "Normal closure")
    }
}
