package com.carenest.domain.repository

data class PaymobNativeCheckoutRequest(
    val publicKey: String,
    val clientSecret: String,
    val appName: String,
    val buttonBackgroundColor: Int,
    val buttonTextColor: Int,
    val saveCardDefault: Boolean,
    val showSaveCard: Boolean,
)

sealed interface PaymobNativeCheckoutResult {
    data class Success(val details: Map<String, String>) : PaymobNativeCheckoutResult
    data object Cancelled : PaymobNativeCheckoutResult
    data object Pending : PaymobNativeCheckoutResult
    data class Failed(val reason: String) : PaymobNativeCheckoutResult
    data object Unknown : PaymobNativeCheckoutResult
}

interface PaymobNativeCheckoutLauncher {
    fun isAvailable(): Boolean = true
    suspend fun launch(request: PaymobNativeCheckoutRequest): Result<PaymobNativeCheckoutResult>
}
