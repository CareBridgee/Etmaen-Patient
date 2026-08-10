package com.carenest.domain.socket

interface SocketServiceController {
    fun startService(requestId: String? = null)
    fun stopService()
}
