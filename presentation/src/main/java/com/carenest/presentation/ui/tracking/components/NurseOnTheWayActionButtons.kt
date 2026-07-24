package com.carenest.presentation.ui.tracking.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.R


@Composable
fun NurseOnTheWayActionButtons(
    onShowQrCodeClick: () -> Unit,
    onCancelClick: () -> Unit,
    isCancelling: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Button(
            onClick = onShowQrCodeClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Theme.colors.primary,
                contentColor = Theme.colors.onPrimary,
            ),
        ) {
            Text(
                text = stringResource(R.string.nurse_on_the_way_show_qr_code_button),
                style = Theme.typography.body.medium,
            )
        }

        Button(
            onClick = onCancelClick,
            enabled = !isCancelling,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Theme.colors.errorContainer,
                contentColor = Theme.colors.onErrorContainer,
                disabledContainerColor = Theme.colors.errorContainer,
                disabledContentColor = Theme.colors.onErrorContainer,
            ),
        ) {
            if (isCancelling) {
                CircularProgressIndicator(
                    modifier = Modifier.height(20.dp),
                    color = Theme.colors.onErrorContainer,
                    strokeWidth = 2.dp,
                )
            } else {
                Text(
                    text = stringResource(R.string.nurse_on_the_way_cancel_button),
                    style = Theme.typography.body.medium,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview(){
    SpTheme {
        NurseOnTheWayActionButtons(
            onShowQrCodeClick = {},
            onCancelClick = {},
            isCancelling = false,
        )
    }
}