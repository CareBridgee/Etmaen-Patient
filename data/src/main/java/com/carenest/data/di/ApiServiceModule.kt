package com.carenest.data.di

import com.carenest.data.source.remote.service.AiChatApiService
import com.carenest.data.source.remote.service.AiChatApiServiceImpl
import com.carenest.data.source.remote.service.AuthApiService
import com.carenest.data.source.remote.service.AuthApiServiceImpl
import com.carenest.data.source.remote.service.CareNestApiService
import com.carenest.data.source.remote.service.CareNestApiServiceImpl
import com.carenest.data.source.remote.service.ProfileApiService
import com.carenest.data.source.remote.service.ProfileApiServiceImpl
import com.carenest.data.source.remote.service.UserApiService
import com.carenest.data.source.remote.service.UserApiServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


import com.carenest.data.source.remote.service.FamilyMembersApiService
import com.carenest.data.source.remote.service.FamilyMembersApiServiceImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class ApiServiceModule {

    @Binds
    abstract fun provideAuthApiService(authApiServiceImpl: AuthApiServiceImpl): AuthApiService

    @Binds
    abstract fun provideProfileApiService(profileApiServiceImpl: ProfileApiServiceImpl): ProfileApiService

    @Binds
    abstract fun provideAiChatApiService(aiChatApiServiceImpl: AiChatApiServiceImpl): AiChatApiService
    @Binds
    abstract fun provideCareNestApiService(careNestApiServiceImpl: CareNestApiServiceImpl): CareNestApiService

    @Binds
    abstract fun provideUserApiService(userApiServiceImpl: UserApiServiceImpl): UserApiService

    @Binds
    abstract fun provideFamilyMembersApiService(impl: FamilyMembersApiServiceImpl): FamilyMembersApiService
}

