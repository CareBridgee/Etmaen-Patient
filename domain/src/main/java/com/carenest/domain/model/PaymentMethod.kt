package com.carenest.domain.model

data class PaymentMethod(
    val id: String,
    val title: String,
    val description: String,
    val iconResName: String,
    val isSelected: Boolean,
    val subDescription: String,
)
