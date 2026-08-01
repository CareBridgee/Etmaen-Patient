package com.carenest.data.socket.logger

import android.util.Log
import javax.inject.Inject

interface SocketLogger {
    fun log(message: String)
    fun error(message: String, throwable: Throwable? = null)
}

class DefaultSocketLogger @Inject constructor() : SocketLogger {
    override fun log(message: String) {
        Log.d("SocketManager", message)
    }

    override fun error(message: String, throwable: Throwable?) {
        Log.e("SocketManager", message, throwable)
    }
}
