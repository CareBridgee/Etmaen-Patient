package com.carenest.data.di

import com.carenest.data.source.remote.service.AuthApiService
import com.carenest.data.source.remote.service.AuthApiServiceImpl
import com.carenest.data.source.remote.service.CareNestApiService
import com.carenest.data.source.remote.service.CareNestApiServiceImpl
import com.carenest.data.source.remote.service.ProfileApiService
import com.carenest.data.source.remote.service.ProfileApiServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
abstract class ApiServiceModule {


    @Binds
    abstract fun provideAuthApiService(authApiServiceImpl: AuthApiServiceImpl): AuthApiService

    @Binds
    abstract fun provideProfileApiService(profileApiServiceImpl: ProfileApiServiceImpl): ProfileApiService

    @Binds
    abstract fun provideCareNestApiService(careNestApiServiceImpl: CareNestApiServiceImpl): CareNestApiService


}
