package com.carenest.presentation.util

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import com.carenest.designsystem.components.dialog.CareNestDialog

@Composable
fun LocationPermissionHandler(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit,
    showRationale: Boolean,
    onRationaleDismissed: () -> Unit,
) {
    val context = LocalContext.current
    val activity = context as? ComponentActivity

    val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val granted = result.values.any { it }
        if (granted) {
            onPermissionGranted()
        } else {
            onPermissionDenied()
        }
    }

    if (showRationale) {
        CareNestDialog(
            title = "Location Permission Required",
            message = "This app needs access to your location to help you select an address on the map.",
            confirmText = "Allow",
            dismissText = "Deny",
            onConfirm = {
                    launcher.launch(permissions)
                    onRationaleDismissed()
            },
            onDismiss = onRationaleDismissed,
        )
    } else {
        LaunchedEffect(Unit) {
            val isGranted = permissions.any {
                ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            }

            if (isGranted) {
                onPermissionGranted()
            } else {
                val shouldShowRationale = activity?.let {
                    ActivityCompat.shouldShowRequestPermissionRationale(it, Manifest.permission.ACCESS_FINE_LOCATION) ||
                            ActivityCompat.shouldShowRequestPermissionRationale(it, Manifest.permission.ACCESS_COARSE_LOCATION)
                } ?: false

                if (shouldShowRationale) {
                    onPermissionDenied() // Parent handles showing rationale
                } else {
                    launcher.launch(permissions)
                }
            }
        }
    }
}
