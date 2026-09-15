package com.carenest.presentation.ui.qrcode

import com.carenest.presentation.core.util.UiText

data class QrCodeState(
    val requestId: String = "",
    val qrData: String = "",
    val isLoading: Boolean = false,
    val error: UiText? = null
)

sealed interface QrCodeIntent {
    data class LoadQrCode(val requestId: String) : QrCodeIntent
    data object BackClicked : QrCodeIntent
    data class RetryClicked(val requestId: String) : QrCodeIntent
}

sealed interface QrCodeEffect {
    data object NavigateBack : QrCodeEffect
    data class NavigateToVisitCompleted(val requestId: String) : QrCodeEffect
}
