package com.carenest.data.socket.stomp

data class StompFrame(
    val command: String,
    val headers: Map<String, String> = emptyMap(),
    val body: String? = null
) {
    companion object {
        const val CONNECT = "CONNECT"
        const val CONNECTED = "CONNECTED"
        const val SUBSCRIBE = "SUBSCRIBE"
        const val UNSUBSCRIBE = "UNSUBSCRIBE"
        const val SEND = "SEND"
        const val MESSAGE = "MESSAGE"
        const val RECEIPT = "RECEIPT"
        const val ERROR = "ERROR"
        const val DISCONNECT = "DISCONNECT"
    }
}
