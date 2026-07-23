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
import com.carenest.data.repository.ProfileRepositoryImpl
import com.carenest.domain.repository.HomeRepository
import com.carenest.domain.repository.ProfileRepository
import com.carenest.data.source.local.datasource.home.HomeDatasource
import com.carenest.data.source.local.datasource.home.HomeFakeDatasourceImpl
import com.carenest.data.source.local.profile.LocalProfileDraftDataSource
import com.carenest.data.source.local.profile.LocalProfileDraftDataSourceImpl
import com.carenest.data.source.local.profile.ProfileFallbackCatalogDataSource
import com.carenest.data.source.local.profile.ProfileFallbackCatalogDataSourceImpl

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
    abstract fun provideProfileRepositoryImpl(profileRepositoryImpl: ProfileRepositoryImpl): ProfileRepository

    //endregion

    //region datasource
    @Binds
    abstract fun provideSettingsDatasource(datastoreImpl: CarenestDatastoreImpl): CarenestDatastore

    @Binds
    abstract fun provideAuthDatasource(authDatasourceImpl: AuthDatasourceImpl): AuthDatasource

    @Binds
    abstract fun provideHomeDatasource(homeDatasourceImpl: HomeFakeDatasourceImpl): HomeDatasource

    @Binds
    abstract fun provideProfileFallbackCatalogDataSource(
        dataSource: ProfileFallbackCatalogDataSourceImpl
    ): ProfileFallbackCatalogDataSource

    @Binds
    abstract fun provideLocalProfileDraftDataSource(
        dataSource: LocalProfileDraftDataSourceImpl
    ): LocalProfileDraftDataSource

    //endregion
}
