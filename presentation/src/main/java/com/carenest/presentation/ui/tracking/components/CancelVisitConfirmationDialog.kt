package com.carenest.presentation.ui.tracking.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.carenest.designsystem.components.dialog.CareNestDialog
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
    CareNestDialog(
        title = title,
        message = message,
        confirmText = confirmText,
        dismissText = dismissText.takeIf { it.isNotEmpty() },
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        confirmColor = Theme.colors.error,
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
