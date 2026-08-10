package com.carenest.data.socket.models

import com.carenest.domain.socket.SocketError
import kotlinx.serialization.Serializable

@Serializable
data class SocketErrorPayloadDto(
    val code: String? = null,
    val message: String? = null,
    val timestamp: String? = null
) {
    fun toDomain() = SocketError.OperationError(
        code = code ?: "UNKNOWN",
        message = message ?: "Operation error occurred",
        timestamp = timestamp ?: ""
    )
}
