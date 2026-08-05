package com.carenest.presentation.ui.qrcode

data class QrCodeState(
    val qrData: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface QrCodeIntent {
    data class LoadQrCode(val requestId: String) : QrCodeIntent
    data object BackClicked : QrCodeIntent
}

sealed interface QrCodeEffect {
    data object NavigateBack : QrCodeEffect
}
