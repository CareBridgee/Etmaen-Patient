package com.carenest.util

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.core.util.Consumer

@Composable
fun HandleMainIntent(
    intent: Intent?,
    onRequestIdFound: (String) -> Unit
) {
    DisposableEffect(intent) {
        intent?.getStringExtra("requestId")?.let { requestId ->
            onRequestIdFound(requestId)
        }
        onDispose { }
    }
}
