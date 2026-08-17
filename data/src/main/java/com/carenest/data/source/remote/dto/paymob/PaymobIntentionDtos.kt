package com.carenest.data.source.remote.dto.paymob

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaymobIntentionRequestDto(
    @SerialName("amount") val amount: Long,
    @SerialName("currency") val currency: String,
    @SerialName("payment_methods") val paymentMethods: List<Int>,
    @SerialName("items") val items: List<PaymobIntentionItemDto>,
    @SerialName("billing_data") val billingData: PaymobBillingDataDto,
    @SerialName("customer") val customer: PaymobCustomerDto,
    @SerialName("special_reference") val specialReference: String,
    @SerialName("extras") val extras: Map<String, String> = mapOf("source" to "carenest_android_wallet"),
)

@Serializable
data class PaymobIntentionItemDto(
    @SerialName("name") val name: String,
    @SerialName("amount") val amount: Long,
    @SerialName("description") val description: String,
    @SerialName("quantity") val quantity: Int,
)

@Serializable
data class PaymobBillingDataDto(
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String,
    @SerialName("email") val email: String,
    @SerialName("phone_number") val phoneNumber: String,
    @SerialName("apartment") val apartment: String = "NA",
    @SerialName("floor") val floor: String = "NA",
    @SerialName("street") val street: String = "NA",
    @SerialName("building") val building: String = "NA",
    @SerialName("shipping_method") val shippingMethod: String = "NA",
    @SerialName("postal_code") val postalCode: String = "NA",
    @SerialName("city") val city: String = "NA",
    @SerialName("country") val country: String = "EG",
    @SerialName("state") val state: String = "NA",
)

@Serializable
data class PaymobCustomerDto(
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String,
    @SerialName("email") val email: String,
)

@Serializable
data class PaymobIntentionResponseDto(
    @SerialName("id") val id: String? = null,
    @SerialName("client_secret") val clientSecret: String? = null,
    @SerialName("status") val status: String? = null,
)
