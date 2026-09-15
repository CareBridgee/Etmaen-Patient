package com.carenest.domain.repository

import com.carenest.domain.model.PaymobNativeCheckoutRequest

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
