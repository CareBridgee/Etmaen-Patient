package com.carenest.data.paymob

import com.carenest.data.BuildConfig
import com.carenest.domain.model.payment.WalletException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymobConfiguration @Inject constructor() : PaymobConfigProvider {
    override fun current(): Result<PaymobConfig> {
        val secretKey = BuildConfig.paymob_secret_key.trim()
        val publicKey = BuildConfig.paymob_public_key.trim()
        val integrationId = BuildConfig.paymob_integration_id.trim().toIntOrNull()
        val baseUrl = BuildConfig.paymob_base_url.trim().ifBlank { DEFAULT_EGYPT_BASE_URL }

        if (secretKey.isBlank() || publicKey.isBlank() || integrationId == null || integrationId <= 0) {
            return Result.failure(WalletException.PaymentConfigurationMissing)
        }

        return Result.success(
            PaymobConfig(
                secretKey = secretKey,
                publicKey = publicKey,
                integrationId = integrationId,
                baseUrl = baseUrl,
            ),
        )
    }

    companion object {
        const val DEFAULT_EGYPT_BASE_URL = "https://accept.paymob.com"
    }
}

interface PaymobConfigProvider {
    fun current(): Result<PaymobConfig>
}

class PaymobConfig internal constructor(
    val secretKey: String,
    val publicKey: String,
    val integrationId: Int,
    val baseUrl: String,
) {
    override fun toString(): String =
        "PaymobConfig(secretKey=<redacted>, publicKey=<redacted>, integrationId=$integrationId, baseUrl=$baseUrl)"
}
