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

import com.carenest.data.repository.HomeRepositoryImpl
import com.carenest.domain.repository.HomeRepository
import com.carenest.data.source.local.datasource.home.HomeDatasource
import com.carenest.data.source.local.datasource.home.HomeFakeDatasourceImpl
import com.carenest.data.repository.GeocodingRepositoryImpl
import com.carenest.data.source.remote.service.GeocodingApiService
import com.carenest.data.source.remote.service.GeocodingApiServiceImpl
import com.carenest.domain.repository.GeocodingRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    //region repository
    @Binds
    abstract fun provideSettingsRepositoryImpl(settingsRepository: SettingsRepositoryImpl): SettingsRepository

    @Binds
    abstract fun provideAuthRepositoryImpl(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    abstract fun provideHomeRepositoryImpl(homeRepositoryImpl: HomeRepositoryImpl): HomeRepository

    @Binds
    abstract fun provideGeocodingRepositoryImpl(impl: GeocodingRepositoryImpl): GeocodingRepository

    @Binds
    abstract fun provideGeocodingApiService(impl: GeocodingApiServiceImpl): GeocodingApiService

    //endregion

    //region datasource
    @Binds
    abstract fun provideSettingsDatasource(datastoreImpl: CarenestDatastoreImpl): CarenestDatastore

    @Binds
    abstract fun provideAuthDatasource(authDatasourceImpl: AuthDatasourceImpl): AuthDatasource

    @Binds
    abstract fun provideHomeDatasource(homeDatasourceImpl: HomeFakeDatasourceImpl): HomeDatasource

    //endregion
}