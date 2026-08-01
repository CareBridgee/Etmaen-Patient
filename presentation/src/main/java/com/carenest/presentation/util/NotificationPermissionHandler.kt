package com.carenest.presentation.util

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

@Composable
fun NotificationPermissionHandler(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit,
    showRationale: Boolean,
    onRationaleDismissed: () -> Unit,
) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        LaunchedEffect(Unit) {
            onPermissionGranted()
        }
        return
    }

    val context = LocalContext.current
    val activity = context as? ComponentActivity

    val permission = Manifest.permission.POST_NOTIFICATIONS

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onPermissionGranted()
        } else {
            onPermissionDenied()
        }
    }

    if (showRationale) {
        AlertDialog(
            onDismissRequest = onRationaleDismissed,
            title = { Text("Notification Permission Required") },
            text = { Text("This app needs notification permissions to show background service status for real-time updates.") },
            confirmButton = {
                TextButton(onClick = {
                    launcher.launch(permission)
                    onRationaleDismissed()
                }) {
                    Text("Allow")
                }
            },
            dismissButton = {
                TextButton(onClick = onRationaleDismissed) {
                    Text("Deny")
                }
            }
        )
    } else {
        LaunchedEffect(Unit) {
            val isGranted = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

            if (isGranted) {
                onPermissionGranted()
            } else {
                val shouldShowRationale = activity?.let {
                    ActivityCompat.shouldShowRequestPermissionRationale(it, permission)
                } ?: false

                if (shouldShowRationale) {
                    onPermissionDenied() // Parent handles showing rationale
                } else {
                    launcher.launch(permission)
                }
            }
        }
    }
}
