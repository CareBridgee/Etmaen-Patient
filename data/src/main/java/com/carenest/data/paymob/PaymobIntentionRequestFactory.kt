package com.carenest.data.paymob

import com.carenest.data.source.remote.dto.paymob.PaymobBillingDataDto
import com.carenest.data.source.remote.dto.paymob.PaymobCustomerDto
import com.carenest.data.source.remote.dto.paymob.PaymobIntentionItemDto
import com.carenest.data.source.remote.dto.paymob.PaymobIntentionRequestDto
import com.carenest.domain.model.home.User
import com.carenest.domain.model.payment.WalletException
import com.carenest.domain.model.payment.WalletTopUpAmount
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymobIntentionRequestFactory @Inject constructor() {
    fun create(
        amount: WalletTopUpAmount,
        integrationId: Int,
        merchantReference: String,
        user: User,
    ): Result<PaymobIntentionRequestDto> {
        val phoneNumber = user.phoneNumber.toPaymobPhoneNumber()
        if (phoneNumber.isBlank()) {
            return Result.failure(WalletException.PaymentConfigurationMissing)
        }

        val firstName = user.firstName.cleanPaymobName().ifBlank { "CareNest" }
        val lastName = user.lastName.cleanPaymobName().ifBlank { "Patient" }
        val email = user.email?.trim()?.takeIf { it.isNotBlank() } ?: "patient@carenest.local"

        return Result.success(
            PaymobIntentionRequestDto(
                amount = amount.minorUnits,
                currency = "EGP",
                paymentMethods = listOf(integrationId),
                items = listOf(
                    PaymobIntentionItemDto(
                        name = "CareNest wallet top-up",
                        amount = amount.minorUnits,
                        description = "CareNest wallet credit",
                        quantity = 1,
                    ),
                ),
                billingData = PaymobBillingDataDto(
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    phoneNumber = phoneNumber,
                ),
                customer = PaymobCustomerDto(
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                ),
                specialReference = merchantReference,
                extras = mapOf(
                    "source" to "carenest_android_wallet",
                    "merchant_reference" to merchantReference,
                ),
            ),
        )
    }

    private fun String?.cleanPaymobName(): String =
        this?.trim()
            ?.filter { it.isLetter() || it == ' ' || it == '-' }
            ?.take(64)
            .orEmpty()

    private fun String?.toPaymobPhoneNumber(): String {
        val value = this?.trim().orEmpty()
        if (value.isBlank()) return ""

        val digits = value.filter(Char::isDigit)
        return when {
            value.startsWith("+") -> value
            digits.startsWith("20") -> "+$digits"
            digits.startsWith("0") && digits.length == 11 -> "+2$digits"
            digits.startsWith("1") && digits.length == 10 -> "+20$digits"
            else -> value
        }
    }
}
