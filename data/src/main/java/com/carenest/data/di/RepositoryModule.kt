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
import com.carenest.data.repository.NurseTrackingRepositoryImpl
import com.carenest.domain.repository.HomeRepository
import com.carenest.data.source.local.datasource.home.HomeDatasource
import com.carenest.data.source.local.datasource.home.HomeFakeDatasourceImpl
import com.carenest.data.repository.GeocodingRepositoryImpl
import com.carenest.data.source.remote.service.GeocodingApiService
import com.carenest.data.source.remote.service.GeocodingApiServiceImpl
import com.carenest.domain.repository.GeocodingRepository
import com.carenest.data.source.remote.datasource.NurseTrackingDataSource
import com.carenest.data.source.remote.datasource.NurseTrackingDataSourceImp
import com.carenest.domain.repository.NurseTrackingRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    //region repository
    @Binds
    @Singleton
    abstract fun provideSettingsRepositoryImpl(settingsRepository: SettingsRepositoryImpl): SettingsRepository

    @Binds
    @Singleton
    abstract fun provideAuthRepositoryImpl(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun provideHomeRepositoryImpl(homeRepositoryImpl: HomeRepositoryImpl): HomeRepository

    @Binds
    abstract fun provideGeocodingRepositoryImpl(impl: GeocodingRepositoryImpl): GeocodingRepository

    @Binds
    abstract fun provideGeocodingApiService(impl: GeocodingApiServiceImpl): GeocodingApiService
    @Binds
    @Singleton
    abstract fun bindNurseTrackingRepository(impl: NurseTrackingRepositoryImpl, ): NurseTrackingRepository

    //endregion

    //region datasource
    @Binds
    @Singleton
    abstract fun provideSettingsDatasource(datastoreImpl: CarenestDatastoreImpl): CarenestDatastore

    @Binds
    @Singleton
    abstract fun provideAuthDatasource(authDatasourceImpl: AuthDatasourceImpl): AuthDatasource

    @Binds
    @Singleton
    abstract fun provideHomeDatasource(homeDatasourceImpl: HomeFakeDatasourceImpl): HomeDatasource

    @Binds
    @Singleton
    abstract fun provideNurseTrackingDataSource(nurseTrackingDataSource: NurseTrackingDataSourceImp): NurseTrackingDataSource

    //endregion
}