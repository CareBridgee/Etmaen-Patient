package com.carenest

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.carenest.presentation.navigation.AppNav
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var deepLinkRequestId by mutableStateOf<String?>(null)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        intent?.getStringExtra("requestId")?.let {
            deepLinkRequestId = it
        }

        setContent {
            AppNav(
                deepLinkRequestId = deepLinkRequestId,
                onDeepLinkHandled = { deepLinkRequestId = null }
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.getStringExtra("requestId")?.let {
            deepLinkRequestId = it
        }
    }
}
