package com.carenest.domain.socket

sealed interface SocketError {
    data class AuthFailed(val reason: String) : SocketError
    data class NetworkError(val cause: Throwable) : SocketError
    data class StompError(val message: String, val body: String?) : SocketError
    data class SubscriptionError(val topic: String, val message: String) : SocketError
    data class ParsingError(val topic: String, val payload: String, val cause: Throwable) : SocketError
    data class OperationError(val code: String, val message: String, val timestamp: String) : SocketError
    data object HeartbeatTimeout : SocketError
}
