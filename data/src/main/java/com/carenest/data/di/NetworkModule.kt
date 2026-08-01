package com.carenest.data.di

import android.util.Log
import com.carenest.data.BuildConfig
import com.carenest.data.source.local.preferences.CarenestDatastore
import com.carenest.data.source.remote.dto.RefreshRequest
import com.carenest.data.source.remote.dto.TokenPairResponse
import com.carenest.domain.config.TemporaryCompleteProfileTestConfig
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

}

internal fun HttpClientConfig<*>.installBearerAuthentication(datastore: CarenestDatastore) {
    install(Auth) {
        bearer {
            loadTokens {
                if (TemporaryCompleteProfileTestConfig.ENABLED) {
                    BearerTokens(TemporaryCompleteProfileTestConfig.ACCESS_TOKEN, "")
                } else {
                    datastore.authTokens.first()?.let { tokens ->
                        BearerTokens(tokens.accessToken, tokens.refreshToken)
                    }
                }
            }
            refreshTokens {
                if (TemporaryCompleteProfileTestConfig.ENABLED) return@refreshTokens null
                val refreshToken = oldTokens?.refreshToken?.takeIf(String::isNotBlank)
                    ?: datastore.authTokens.first()?.refreshToken
                    ?: return@refreshTokens null

                val response = client.post("api/v1/auth/refresh") {
                    markAsRefreshTokenRequest()
                    contentType(ContentType.Application.Json)
                    setBody(RefreshRequest(refreshToken))
                }
                
                if (response.status.value == 401 || response.status.value == 403) {
                    Log.e("NetworkModule", "Refresh token expired or invalid (Status ${response.status.value}). Logging out.")
                    datastore.clearAuthTokens()
                    datastore.setLoggedIn(false)
                    return@refreshTokens null
                }

                if (!response.status.isSuccess()) {
                    return@refreshTokens null
                }

                val refreshed = response.body<TokenPairResponse>()
                val accessToken = refreshed.accessToken?.takeIf(String::isNotBlank)
                    ?: return@refreshTokens null
                val newRefreshToken = refreshed.refreshToken?.takeIf(String::isNotBlank)
                    ?: return@refreshTokens null

                datastore.saveAuthTokens(accessToken, newRefreshToken)
                BearerTokens(accessToken, newRefreshToken)
            }
            sendWithoutRequest { request ->
                val requestUrl = request.url.build()
                val path = requestUrl.encodedPath.let { if (it.startsWith("/")) it else "/$it" }
                val isBackendHost = requestUrl.host.isBlank() || requestUrl.host.equals(BACKEND_HOST, ignoreCase = true)
                isBackendHost && path.startsWith("/api/v1") && path !in PUBLIC_AUTH_PATHS
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
