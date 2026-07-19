package com.carenest.data.di

import com.carenest.data.source.remote.service.AuthApiService
import com.carenest.data.source.remote.service.AuthApiServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
abstract class ApiServiceModule {


    @Binds
    abstract fun provideAuthApiService(authApiServiceImpl: AuthApiServiceImpl): AuthApiService



}