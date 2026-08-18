package com.carenest.presentation.util

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.carenest.designsystem.components.dialog.CareNestDialog
import com.carenest.designsystem.R

@Composable
fun AudioPermissionHandler(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit,
    showRationale: Boolean,
    onRationaleDismissed: () -> Unit,
) {
    val context = LocalContext.current
    val activity = context as? ComponentActivity

    val permission = Manifest.permission.RECORD_AUDIO

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
        CareNestDialog(
            title = stringResource(R.string.permission_audio_title),
            message = stringResource(R.string.permission_audio_rationale),
            confirmText = stringResource(R.string.permission_allow),
            dismissText = stringResource(R.string.permission_deny),
            onConfirm = {
                launcher.launch(permission)
                onRationaleDismissed()
            },
            onDismiss = onRationaleDismissed,
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
                    onPermissionDenied()
                } else {
                    launcher.launch(permission)
                }
            }
        }
    }
}
