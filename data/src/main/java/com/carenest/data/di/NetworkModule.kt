package com.carenest.data.di

import com.carenest.data.BuildConfig
import com.carenest.data.utils.KtorPluginKeys
import com.carenest.data.utils.authenticationplugin
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
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
    ): HttpClient =
        HttpClient(Android) {
            install(ContentNegotiation) {
                json(json)
            }

            install(Logging) {
                level = LogLevel.BODY
            }

//            install(authenticationplugin)

            defaultRequest {
                url(BuildConfig.base_url)
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