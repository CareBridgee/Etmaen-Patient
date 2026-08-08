package com.carenest.data.di

import com.carenest.data.BuildConfig
import com.carenest.data.socket.SocketManagerImpl
import com.carenest.data.socket.logger.DefaultSocketLogger
import com.carenest.data.socket.logger.SocketLogger
import com.carenest.data.socket.stomp.StompClient
import com.carenest.domain.socket.SocketConnectionManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.pingInterval
import kotlin.time.Duration.Companion.seconds
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SocketModule {

    @Binds
    @Singleton
    abstract fun bindSocketConnectionManager(
        impl: SocketManagerImpl
    ): SocketConnectionManager

    @Binds
    @Singleton
    abstract fun bindSocketLogger(
        impl: DefaultSocketLogger
    ): SocketLogger

    companion object {
        @Provides
        @Singleton
        @SocketHttpClient
        fun provideSocketHttpClient(): HttpClient {
            return HttpClient(Android) {
                install(WebSockets) {
                    pingInterval = 10.seconds
                }
                if (BuildConfig.DEBUG) {
                    install(Logging) {
                        logger = Logger.ANDROID
                        level = LogLevel.BODY
                    }
                }
            }
        }

        @Provides
        @Singleton
        fun provideStompClient(@SocketHttpClient httpClient: HttpClient): StompClient {
            return StompClient(httpClient)
        }
    }
}
