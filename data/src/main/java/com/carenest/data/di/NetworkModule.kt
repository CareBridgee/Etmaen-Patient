package com.carenest.data.di

import android.util.Log
import com.carenest.data.BuildConfig
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

}

internal fun HttpClientConfig<*>.installBearerAuthentication(datastore: CarenestDatastore) {
    install(DynamicAuthPlugin) {
        this.datastore = datastore
    }
}

class DynamicAuthPluginConfig {
    var datastore: CarenestDatastore? = null
}

val DynamicAuthPlugin = createClientPlugin("DynamicAuthPlugin", ::DynamicAuthPluginConfig) {
    val datastore = pluginConfig.datastore ?: return@createClientPlugin

    onRequest { request, _ ->
        val requestUrl = request.url.build()
        val path = requestUrl.encodedPath.let { if (it.startsWith("/")) it else "/$it" }
        val isBackendHost = requestUrl.host.isBlank() || requestUrl.host.equals(BACKEND_HOST, ignoreCase = true)
        val isProtected = isBackendHost && path.startsWith("/api/v1") && path !in PUBLIC_AUTH_PATHS

        if (isProtected) {
            val tokens = datastore.authTokens.first()
            tokens?.accessToken?.takeIf(String::isNotBlank)?.let { token ->
                request.headers[io.ktor.http.HttpHeaders.Authorization] = "Bearer $token"
            }
        }
    }

    onResponse { response ->
        if (response.status.value == 401 || response.status.value == 403) {
            val requestUrl = response.call.request.url
            val path = requestUrl.encodedPath.let { if (it.startsWith("/")) it else "/$it" }
            if (path !in PUBLIC_AUTH_PATHS) {
                Log.e("NetworkModule", "Auth error ($path - Status ${response.status.value}). Clearing user session.")
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
