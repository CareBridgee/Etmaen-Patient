package com.carenest.service

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.carenest.domain.socket.SocketServiceController
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SocketServiceControllerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SocketServiceController {

    override fun startService(requestId: String?) {
        val intent = Intent(context, SocketForegroundService::class.java).apply {
            putExtra("requestId", requestId)
        }
        ContextCompat.startForegroundService(context, intent)
    }

    override fun stopService() {
        val intent = Intent(context, SocketForegroundService::class.java)
        context.stopService(intent)
    }
}
