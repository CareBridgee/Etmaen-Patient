package com.carenest.di

import com.carenest.domain.socket.SocketServiceController
import com.carenest.service.SocketServiceControllerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {

    @Binds
    @Singleton
    abstract fun bindSocketServiceController(
        impl: SocketServiceControllerImpl
    ): SocketServiceController
}
