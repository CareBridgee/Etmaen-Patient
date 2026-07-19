package com.carenest.data.di

import com.carenest.data.repository.AuthRepositoryImpl
import com.carenest.data.repository.SettingsRepositoryImpl
import com.carenest.data.source.local.preferences.CarenestDatastore
import com.carenest.data.source.local.preferences.CarenestDatastoreImpl
import com.carenest.data.source.remote.datasource.auth.AuthDatasource
import com.carenest.data.source.remote.datasource.auth.AuthDatasourceImpl
import com.carenest.domain.repository.AuthRepository
import com.carenest.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    //region repository
    @Binds
    abstract fun provideSettingsRepositoryImpl(settingsRepository: SettingsRepositoryImpl): SettingsRepository

    @Binds
    abstract fun provideAuthRepositoryImpl(authRepositoryImpl: AuthRepositoryImpl): AuthRepository


    //endregion

    //region datasource
    @Binds
    abstract fun provideSettingsDatasource(datastoreImpl: CarenestDatastoreImpl): CarenestDatastore

    @Binds
    abstract fun provideAuthDatasource(authDatasourceImpl: AuthDatasourceImpl): AuthDatasource


    //endregion
}