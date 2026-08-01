package com.carenest.data.di

import com.carenest.domain.socket.SocketConnectionManager
import com.carenest.data.socket.SocketManagerImpl
import com.carenest.data.socket.logger.DefaultSocketLogger
import com.carenest.data.socket.logger.SocketLogger
import com.carenest.data.socket.stomp.StompClient
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
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
        @SocketOkHttpClient
        fun provideSocketOkHttpClient(): OkHttpClient {
            // A separate OkHttpClient for WebSockets with different timeout config than REST calls.
            return OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS) // Disable read timeout for WebSockets
                .pingInterval(0, TimeUnit.MILLISECONDS) // Using STOMP heartbeats instead
                .build()
        }

        @Provides
        @Singleton
        fun provideStompClient(@SocketOkHttpClient okHttpClient: OkHttpClient): StompClient {
            return StompClient(okHttpClient)
        }
    }
}
