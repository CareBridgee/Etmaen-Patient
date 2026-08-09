package com.carenest.presentation.ui.tracking.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.R

@Composable
fun CancelVisitConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    title: String = stringResource(R.string.nurse_on_the_way_cancel_dialog_title),
    message: String = stringResource(R.string.nurse_on_the_way_cancel_dialog_message),
    confirmText: String = stringResource(R.string.nurse_on_the_way_cancel_dialog_confirm),
    dismissText: String = stringResource(R.string.nurse_on_the_way_cancel_dialog_dismiss),
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Theme.colors.surface,
        title = {
            Text(
                text = title,
                style = Theme.typography.title.copy(fontWeight = FontWeight.Bold),
                color = Theme.colors.primaryFont,
            )
        },
        text = {
            Text(
                text = message,
                style = Theme.typography.body.medium,
                color = Theme.colors.secondaryFont,
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = confirmText,
                    style = Theme.typography.body.medium,
                    color = Theme.colors.error,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = dismissText,
                    style = Theme.typography.body.medium,
                    color = Theme.colors.secondaryFont,
                )
            }
        },
    )
}
@Preview(showBackground = true)
@Composable
private fun Preview(){
    SpTheme {
        CancelVisitConfirmationDialog(
            onConfirm = {},
            onDismiss = {},
        )
    }
}