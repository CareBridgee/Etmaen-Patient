package com.carenest.data.di

import com.carenest.data.repository.AuthRepositoryImpl
import com.carenest.data.repository.ChatRepositoryImpl
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
import com.carenest.data.repository.NurseTrackingRepositoryImpl
import com.carenest.data.repository.VisitSummaryRepositoryImpl
import com.carenest.domain.repository.HomeRepository
import com.carenest.domain.repository.ProfileRepository
import com.carenest.data.repository.GeocodingRepositoryImpl
import com.carenest.data.source.remote.datasource.CareNestRemoteDataSourceImpl
import com.carenest.data.source.remote.datasource.CareNestRemoteDatasource
import com.carenest.data.source.remote.service.GeocodingApiService
import com.carenest.data.source.remote.service.GeocodingApiServiceImpl
import com.carenest.domain.repository.GeocodingRepository
import com.carenest.data.source.remote.datasource.NurseTrackingDataSource
import com.carenest.data.source.remote.datasource.NurseTrackingDataSourceImp
import com.carenest.data.source.remote.datasource.VisitSummaryDataSource
import com.carenest.data.source.remote.datasource.VisitSummaryDataSourceImp
import com.carenest.domain.repository.NurseTrackingRepository
import com.carenest.domain.repository.VisitSummaryRepository
import javax.inject.Singleton
import com.carenest.data.source.remote.datasource.ChatDataSource
import com.carenest.data.source.remote.datasource.ChatDataSourceImp
import com.carenest.domain.repository.ChatRepository

import com.carenest.data.repository.ChatSocketRepositoryImpl
import com.carenest.data.repository.NotificationSocketRepositoryImpl
import com.carenest.data.repository.ReservationSocketRepositoryImpl
import com.carenest.domain.repository.ChatSocketRepository
import com.carenest.domain.repository.NotificationSocketRepository
import com.carenest.domain.repository.ReservationSocketRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindReservationSocketRepository(impl: ReservationSocketRepositoryImpl): ReservationSocketRepository

    @Binds
    @Singleton
    abstract fun bindChatSocketRepository(impl: ChatSocketRepositoryImpl): ChatSocketRepository

    @Binds
    @Singleton
    abstract fun bindNotificationSocketRepository(impl: NotificationSocketRepositoryImpl): NotificationSocketRepository

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
    abstract fun provideProfileRepositoryImpl(profileRepositoryImpl: ProfileRepositoryImpl): ProfileRepository

    @Binds
    @Singleton
    abstract fun bindNurseTrackingRepository(impl: NurseTrackingRepositoryImpl, ): NurseTrackingRepository

    @Binds
    @Singleton
    abstract fun bindVisitSummaryRepository(impl: VisitSummaryRepositoryImpl): VisitSummaryRepository
  
    @Binds
    @Singleton
    abstract fun bindChatRepository(impl: ChatRepositoryImpl): ChatRepository

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
    abstract fun bindChatDataSource(chatDatsSource: ChatDataSourceImp): ChatDataSource

    @Binds
    @Singleton
    abstract fun provideNurseTrackingDataSource(nurseTrackingDataSource: NurseTrackingDataSourceImp): NurseTrackingDataSource

    @Binds
    @Singleton
    abstract fun bindVisitSummaryDataSource(visitSummaryDataSource: VisitSummaryDataSourceImp): VisitSummaryDataSource

    @Binds
    @Singleton
    abstract fun bindCareNestRemoteDatasource(careNestRemoteDatasource: CareNestRemoteDataSourceImpl): CareNestRemoteDatasource

    //endregion
}
