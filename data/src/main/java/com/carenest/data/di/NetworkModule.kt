package com.carenest.data.di

import android.util.Log
import com.carenest.data.BuildConfig
import com.carenest.data.paymob.PaymobConfiguration
import com.carenest.data.source.local.preferences.CarenestDatastore
import com.carenest.data.source.remote.dto.RefreshRequest
import com.carenest.data.source.remote.dto.TokenPairResponse
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.call.body
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.Url
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.coroutines.flow.first
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json =
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = true
            explicitNulls = false
        }

    @Provides
    @Singleton
    fun provideHttpClient(
        json: Json,
        datastore: CarenestDatastore,
    ): HttpClient =
        HttpClient(Android) {
            install(ContentNegotiation) {
                json(json)
            }

            installBearerAuthentication(datastore)

            defaultRequest {
                url(BuildConfig.base_url)
            }

            if (BuildConfig.DEBUG) {
                install(Logging) {
                    logger = Logger.ANDROID
                    level = LogLevel.BODY
                }
            }
        }

    @Provides
    @Singleton
    @AuthHttpClient
    fun provideAuthHttpClient(
        json: Json,
    ): HttpClient =
        HttpClient(Android) {
            install(ContentNegotiation) {
                json(json)
            }

            defaultRequest {
                url(BuildConfig.base_url)
            }

            if (BuildConfig.DEBUG) {
                install(Logging) {
                    logger = Logger.ANDROID
                    level = LogLevel.BODY
                }
            }
        }

    @Provides
    @Singleton
    @Named("locationiq")
    fun provideLocationIqHttpClient(
        json: Json,
    ): HttpClient =
        HttpClient(Android) {
            install(ContentNegotiation) {
                json(json)
            }

            install(Logging) {
                level = LogLevel.BODY
            }
        }

    @Provides
    @Singleton
    @PaymobHttpClient
    fun providePaymobHttpClient(
        json: Json,
    ): HttpClient =
        HttpClient(Android) {
            install(ContentNegotiation) {
                json(json)
            }

            installPaymobSecretAuthorization(
                secretKey = BuildConfig.paymob_secret_key,
                allowedHost = Url(paymobBaseUrl()).host,
            )

            defaultRequest {
                url(paymobBaseUrl())
            }
        }

}

internal fun HttpClientConfig<*>.installBearerAuthentication(datastore: CarenestDatastore) {
    install(Auth) {
        bearer {
            sendWithoutRequest { request ->
                val requestUrl = request.url.build()
                val path = requestUrl.encodedPath.let { if (it.startsWith("/")) it else "/$it" }
                val isBackendHost = requestUrl.host.isBlank() || requestUrl.host.equals(BACKEND_HOST, ignoreCase = true)
                isBackendHost && path.startsWith("/api/v1") && path !in PUBLIC_AUTH_PATHS
            }

            loadTokens {
                val tokens = datastore.authTokens.first()
                tokens?.accessToken?.takeIf(String::isNotBlank)?.let { access ->
                    BearerTokens(access, tokens.refreshToken)
                }
            }

            refreshTokens {
                val responseUrl = response.call.request.url
                val path = responseUrl.encodedPath.let { if (it.startsWith("/")) it else "/$it" }
                val isBackendHost = responseUrl.host.isBlank() || responseUrl.host.equals(BACKEND_HOST, ignoreCase = true)

                if (!isBackendHost || path in PUBLIC_AUTH_PATHS) {
                    return@refreshTokens null
                }

                val tokens = datastore.authTokens.first()
                val refreshToken = tokens?.refreshToken?.takeIf(String::isNotBlank)

                if (refreshToken == null) {
                    Log.e("NetworkModule", "No refresh token available. Clearing user session.")
                    datastore.clearAuthTokens()
                    datastore.clearUserId()
                    datastore.setLoggedIn(false)
                    return@refreshTokens null
                }

                val newTokens = runCatching {
                    val refreshResponse = client.post("api/v1/auth/refresh") {
                        contentType(ContentType.Application.Json)
                        setBody(RefreshRequest(refreshToken))
                    }
                    if (refreshResponse.status.isSuccess()) {
                        val tokenPair = refreshResponse.body<TokenPairResponse>()
                        val newAccess = tokenPair.accessToken
                        val newRefresh = tokenPair.refreshToken ?: refreshToken
                        if (!newAccess.isNullOrBlank()) {
                            datastore.saveAuthTokens(newAccess, newRefresh)
                            BearerTokens(newAccess, newRefresh)
                        } else null
                    } else null
                }.getOrNull()

                if (newTokens == null) {
                    Log.e("NetworkModule", "Automatic token refresh failed. Clearing user session.")
                    datastore.clearAuthTokens()
                    datastore.clearUserId()
                    datastore.setLoggedIn(false)
                }

                newTokens
            }
        }
    }

    install(ForbiddenHandlerPlugin) {
        this.datastore = datastore
    }
}

class ForbiddenHandlerPluginConfig {
    var datastore: CarenestDatastore? = null
}

val ForbiddenHandlerPlugin = createClientPlugin("ForbiddenHandlerPlugin", ::ForbiddenHandlerPluginConfig) {
    val datastore = pluginConfig.datastore ?: return@createClientPlugin

    onResponse { response ->
        if (response.status.value == 403) {
            val requestUrl = response.call.request.url
            val path = requestUrl.encodedPath.let { if (it.startsWith("/")) it else "/$it" }
            val isBackendHost = requestUrl.host.isBlank() || requestUrl.host.equals(BACKEND_HOST, ignoreCase = true)
            if (isBackendHost && path !in PUBLIC_AUTH_PATHS) {
                Log.e("NetworkModule", "403 Forbidden. Clearing user session.")
                datastore.clearAuthTokens()
                datastore.clearUserId()
                datastore.setLoggedIn(false)
            }
        }
    }
}

private val BACKEND_HOST = Url(BuildConfig.base_url).host

private val PUBLIC_AUTH_PATHS = setOf(
    "/api/v1/auth/login",
    "/api/v1/auth/dev/request-otp",
    "/api/v1/auth/verify-otp",
    "/api/v1/auth/refresh"
)

private val SafeNetworkLogging = createClientPlugin("SafeNetworkLogging") {
    onRequest { request, _ ->
        val finalUrl = request.url.build()
        Log.d(
            NETWORK_LOG_TAG,
            "request method=${request.method.value} host=${finalUrl.host} path=${finalUrl.encodedPath}"
        )
    }
    onResponse { response ->
        val request = response.call.request
        Log.d(
            NETWORK_LOG_TAG,
            "response method=${request.method.value} host=${request.url.host} " +
                "path=${request.url.encodedPath} status=${response.status.value}"
        )
    }
}

private const val NETWORK_LOG_TAG = "CareNestHttp"

internal fun HttpClientConfig<*>.installPaymobSecretAuthorization(
    secretKey: String,
    allowedHost: String,
) {
    install(PaymobSecretAuthPlugin) {
        this.secretKey = secretKey
        this.allowedHost = allowedHost
    }
}

class PaymobSecretAuthPluginConfig {
    var secretKey: String = ""
    var allowedHost: String = ""
}

val PaymobSecretAuthPlugin = createClientPlugin(
    "PaymobSecretAuthPlugin",
    ::PaymobSecretAuthPluginConfig,
) {
    val secretKey = pluginConfig.secretKey
    val allowedHost = pluginConfig.allowedHost

    onRequest { request, _ ->
        val requestUrl = request.url.build()
        if (
            secretKey.isNotBlank() &&
            allowedHost.isNotBlank() &&
            requestUrl.host.equals(allowedHost, ignoreCase = true)
        ) {
            request.headers[io.ktor.http.HttpHeaders.Authorization] = "Token $secretKey"
        }
    }
}

private fun paymobBaseUrl(): String =
    BuildConfig.paymob_base_url.trim().ifBlank { PaymobConfiguration.DEFAULT_EGYPT_BASE_URL }
