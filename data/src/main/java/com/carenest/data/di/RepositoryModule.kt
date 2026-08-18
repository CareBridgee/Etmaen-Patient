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
import com.carenest.data.paymob.PaymobConfigProvider
import com.carenest.data.paymob.PaymobConfiguration
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
import com.carenest.data.repository.UserRepositoryImpl
import com.carenest.data.repository.PaymobWalletTopUpPaymentGateway
import com.carenest.data.repository.WalletOperationGuardRepositoryImpl
import com.carenest.data.repository.WalletRepositoryImpl
import com.carenest.data.repository.WalletTopUpAttemptRepositoryImpl
import com.carenest.data.source.local.datasource.UserLocalDataSource
import com.carenest.data.source.local.datasource.UserLocalDataSourceImpl
import com.carenest.data.source.remote.datasource.user.UserRemoteDataSource
import com.carenest.data.source.remote.datasource.user.UserRemoteDataSourceImpl
import com.carenest.domain.repository.UserRepository
import com.carenest.domain.repository.WalletOperationGuardRepository
import com.carenest.domain.repository.WalletRepository
import com.carenest.domain.repository.WalletTopUpAttemptRepository
import com.carenest.domain.repository.WalletTopUpPaymentGateway
import com.carenest.data.repository.AiChatRepositoryImpl
import com.carenest.domain.repository.AiChatRepository

import com.carenest.data.repository.FamilyMembersRepositoryImpl
import com.carenest.domain.repository.FamilyMembersRepository
import com.carenest.data.source.remote.datasource.FamilyMembersDataSource
import com.carenest.data.source.remote.datasource.FamilyMembersDataSourceImpl
import com.carenest.data.source.remote.datasource.WalletRemoteDataSource
import com.carenest.data.source.remote.datasource.WalletRemoteDataSourceImpl

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
    abstract fun provideFamilyMembersRepository(impl: FamilyMembersRepositoryImpl): FamilyMembersRepository

    @Binds
    @Singleton
    abstract fun provideSettingsRepositoryImpl(settingsRepository: SettingsRepositoryImpl): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindPaymobConfigProvider(impl: PaymobConfiguration): PaymobConfigProvider

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

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindWalletRepository(impl: WalletRepositoryImpl): WalletRepository

    @Binds
    @Singleton
    abstract fun bindWalletOperationGuardRepository(
        impl: WalletOperationGuardRepositoryImpl,
    ): WalletOperationGuardRepository

    @Binds
    @Singleton
    abstract fun bindWalletTopUpPaymentGateway(
        impl: PaymobWalletTopUpPaymentGateway,
    ): WalletTopUpPaymentGateway

    @Binds
    @Singleton
    abstract fun bindWalletTopUpAttemptRepository(
        impl: WalletTopUpAttemptRepositoryImpl,
    ): WalletTopUpAttemptRepository

    @Binds
    @Singleton
    abstract fun bindAiChatRepository(impl: AiChatRepositoryImpl): AiChatRepository

    @Binds
    @Singleton
    abstract fun bindImageUploader(impl: com.carenest.data.repository.CloudinaryImageUploaderImpl): com.carenest.domain.repository.ImageUploader

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

    @Binds
    @Singleton
    abstract fun bindUserRemoteDataSource(impl: UserRemoteDataSourceImpl): UserRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindWalletRemoteDataSource(impl: WalletRemoteDataSourceImpl): WalletRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindUserLocalDataSource(impl: UserLocalDataSourceImpl): UserLocalDataSource

    @Binds
    @Singleton
    abstract fun provideFamilyMembersDataSource(impl: FamilyMembersDataSourceImpl): FamilyMembersDataSource

    //endregion
}
