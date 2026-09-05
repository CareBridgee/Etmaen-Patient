package com.carenest.presentation.paymob

import android.util.Log
import com.carenest.domain.model.PaymobNativeCheckoutRequest
import com.carenest.domain.model.payment.WalletException
import com.carenest.domain.repository.PaymobNativeCheckoutLauncher
import com.carenest.domain.repository.PaymobNativeCheckoutResult
import com.paymob.paymob_sdk.PaymobSdk
import com.paymob.paymob_sdk.ui.PaymobSdkListener
import java.util.HashMap
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext

@Singleton
class PaymobNativeCheckoutLauncherImpl @Inject constructor(
    private val currentActivityProvider: CurrentActivityProvider,
) : PaymobNativeCheckoutLauncher {
    override fun isAvailable(): Boolean = true

    override suspend fun launch(
        request: PaymobNativeCheckoutRequest,
    ): Result<PaymobNativeCheckoutResult> = withContext(Dispatchers.Main.immediate) {
        val activity = currentActivityProvider.currentActivity()
            ?: return@withContext Result.failure(WalletException.PaymentUnavailable)

        suspendCancellableCoroutine { continuation ->
            val listener = object : PaymobSdkListener {
                override fun onSuccess(payResponse: HashMap<String, String?>) {
                    Log.d(TAG, "Native checkout success. responseKeys=${payResponse.keys.sorted()}")
                    if (continuation.isActive) {
                        continuation.resume(
                            Result.success(
                                PaymobNativeCheckoutResult.Success(
                                    payResponse.mapNotNull { (key, value) ->
                                        value?.let { key to it }
                                    }.toMap(),
                                ),
                            ),
                        )
                    }
                }

                override fun onFailure(msg: String?) {
                    Log.d(TAG, "Native checkout failed. reason=${msg.orEmpty().take(MAX_LOG_REASON_LENGTH)}")
                    if (continuation.isActive) {
                        continuation.resume(
                            Result.success(
                                PaymobNativeCheckoutResult.Failed(
                                    msg.orEmpty().ifBlank { "Payment failed" },
                                ),
                            ),
                        )
                    }
                }

                override fun onPending() {
                    Log.d(TAG, "Native checkout pending.")
                    if (continuation.isActive) {
                        continuation.resume(Result.success(PaymobNativeCheckoutResult.Pending))
                    }
                }
            }

            runCatching {
                Log.d(TAG, "Starting native checkout.")
                PaymobSdk.Builder(
                    activity,
                    request.clientSecret,
                    request.publicKey,
                    listener,
                )
                    .setButtonBackgroundColor(request.buttonBackgroundColor)
                    .setButtonTextColor(request.buttonTextColor)
                    .showSaveCard(request.showSaveCard)
                    .saveCardByDefault(request.saveCardDefault)
                    .setAppName(request.appName)
                    .showTransactionResult(false)
                    .build()
                    .start()
            }.onFailure { error ->
                Log.d(TAG, "Native checkout could not start. reason=${error.message.orEmpty().take(MAX_LOG_REASON_LENGTH)}")
                if (continuation.isActive) {
                    continuation.resume(Result.failure(error))
                }
            }
        }
    }

    private companion object {
        const val TAG = "CareNestPaymob"
        const val MAX_LOG_REASON_LENGTH = 120
    }
}
