package com.carenest.domain.model

data class PaymentMethod(
    val id: String,
    val title: String,
    val description: String,
    val iconResName: String,
    val isSelected: Boolean,
    val subDescription: String,
) {
    companion object {
        val COD = PaymentMethod(
            id = "cod",
            title = "Cash on Delivery",
            description = "Pay when the service is completed",
            iconResName = "ic_wallet",
            subDescription = "",
            isSelected = true
        )
        val CREDIT = PaymentMethod(
            id = "credit",
            title = "Account Credit",
            description = "Use your available CareNest credit",
            iconResName = "ic_wallet",
            subDescription = "",
            isSelected = false
        )
        val PAYMOB = PaymentMethod(
            id = "paymob",
            title = "Credit/Debit Card via Paymob",
            description = "Pay securely using your card",
            iconResName = "ic_wallet",
            subDescription = "Visa, MasterCard, Meeza",
            isSelected = false
        )
    }
}
