package com.carenest.presentation.ui.qrcode

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.carenest.designsystem.components.emptystate.EmptyState
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.R
import com.carenest.presentation.core.mvi.ObserveEffect
import com.carenest.presentation.navigation.ScreenTopBar
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter

@Composable
fun QrCodeScreen(
    requestId: String,
    onNavigateBack: () -> Unit,
    viewModel: QrCodeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(requestId) {
        viewModel.onEvent(QrCodeIntent.LoadQrCode(requestId))
    }

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            QrCodeEffect.NavigateBack -> onNavigateBack()
        }
    }

    QrCodeScreenContent(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun QrCodeScreenContent(
    state: QrCodeState,
    onEvent: (QrCodeIntent) -> Unit
) {
    ScreenTopBar(
        title = stringResource(R.string.qr_code_title),
        onLeadingClick = { onEvent(QrCodeIntent.BackClicked) }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.backGround)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        BasicText(
            text = stringResource(R.string.qr_code_header),
            style = Theme.typography.title.copy(
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontSize = 22.sp,
                color = Theme.colors.primaryFont
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        BasicText(
            text = stringResource(R.string.qr_code_subtitle),
            style = Theme.typography.body.medium.copy(
                color = Theme.colors.secondaryFont,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(48.dp))

        Box(
            modifier = Modifier
                .size(280.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(color = Theme.colors.primary)
            } else if (state.error != null) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(16.dp)
                ) {
                    BasicText(
                        text = state.error,
                        style = Theme.typography.body.small.copy(
                            color = Theme.colors.error,
                            textAlign = TextAlign.Center
                        )
                    )
                    Spacer(modifier = Modifier.height(Theme.spacing.medium))
                    androidx.compose.material3.TextButton(
                        onClick = { onEvent(QrCodeIntent.RetryClicked(state.requestId)) }
                    ) {
                        Text(
                            text = stringResource(R.string.home_error_retry),
                            style = Theme.typography.body.small.copy(
                                color = Theme.colors.primary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            } else if (state.qrData.isNotEmpty()) {
                val bitmap = remember(state.qrData) {
                    generateQrCode(state.qrData, 512)
                }
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Theme.colors.primary.copy(alpha = 0.1f))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            BasicText(
                text = stringResource(R.string.qr_code_instructions),
                style = Theme.typography.body.small.copy(
                    color = Theme.colors.primary,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}

private fun generateQrCode(text: String, size: Int): Bitmap {
    val bitMatrix = MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, size, size)
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
    for (x in 0 until size) {
        for (y in 0 until size) {
            bitmap.setPixel(
                x, y,
                if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE
            )
        }
    }
    return bitmap
}

@Preview(showBackground = true)
@Composable
private fun QrCodeScreenPreview() {
    SpTheme {
        QrCodeScreenContent(
            state = QrCodeState(qrData = "test"),
            onEvent = {}
        )
    }
}
