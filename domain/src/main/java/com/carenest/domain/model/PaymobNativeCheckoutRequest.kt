package com.carenest.domain.model

data class PaymobNativeCheckoutRequest(
    val publicKey: String,
    val clientSecret: String,
    val appName: String,
    val buttonBackgroundColor: Int,
    val buttonTextColor: Int,
    val saveCardDefault: Boolean,
    val showSaveCard: Boolean,
)