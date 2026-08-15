package com.carenest.presentation.util

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.R

@Composable
fun NotificationPermissionHandler(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit,
    showRationale: Boolean = false,
    onRationaleDismissed: () -> Unit = {},
) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        LaunchedEffect(Unit) {
            onPermissionGranted()
        }
        return
    }

    val context = LocalContext.current
    val permission = Manifest.permission.POST_NOTIFICATIONS

    var showSettingsAlert by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onPermissionGranted()
        } else {
            showSettingsAlert = true
        }
    }

    if (showSettingsAlert) {
        AlertDialog(
            onDismissRequest = {
                showSettingsAlert = false
                onPermissionDenied()
            },
            title = {
                Text(
                    text = stringResource(R.string.notification_permission_settings_title),
                    style = Theme.typography.title,
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.notification_permission_settings_message),
                    style = Theme.typography.body.medium,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showSettingsAlert = false
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", context.packageName, null)
                    ).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                    onPermissionDenied()
                }) {
                    Text(
                        text = stringResource(R.string.notification_permission_open_settings),
                        color = Theme.colors.primary,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showSettingsAlert = false
                    onPermissionDenied()
                }) {
                    Text(
                        text = stringResource(R.string.notification_permission_deny),
                        color = Theme.colors.secondaryFont,
                    )
                }
            },
            shape = Theme.shapes.large,
            containerColor = Theme.colors.surface,
            titleContentColor = Theme.colors.primaryFont,
            textContentColor = Theme.colors.secondaryFont,
        )
    } else if (showRationale) {
        AlertDialog(
            onDismissRequest = onRationaleDismissed,
            title = {
                Text(
                    text = stringResource(R.string.notification_permission_title),
                    style = Theme.typography.title,
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.notification_permission_rationale),
                    style = Theme.typography.body.medium,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    launcher.launch(permission)
                    onRationaleDismissed()
                }) {
                    Text(
                        text = stringResource(R.string.notification_permission_allow),
                        color = Theme.colors.primary,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = onRationaleDismissed) {
                    Text(
                        text = stringResource(R.string.notification_permission_deny),
                        color = Theme.colors.secondaryFont,
                    )
                }
            },
            shape = Theme.shapes.large,
            containerColor = Theme.colors.surface,
            titleContentColor = Theme.colors.primaryFont,
            textContentColor = Theme.colors.secondaryFont,
        )
    } else {
        LaunchedEffect(Unit) {
            val isGranted = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

            if (isGranted) {
                onPermissionGranted()
            } else {
                launcher.launch(permission)
            }
        }
    }
}
