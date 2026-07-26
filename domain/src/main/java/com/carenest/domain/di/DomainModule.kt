package com.carenest.domain.di

import com.carenest.domain.repository.AuthRepository
import com.carenest.domain.repository.SettingsRepository
import com.carenest.domain.usecase.auth.LoginWithPhoneUseCase
import com.carenest.domain.usecase.auth.RequestDevOtpUseCase
import com.carenest.domain.usecase.auth.VerifyOtpUseCase
import com.carenest.domain.usecase.settings.GetOnboardingStatusUseCase
import com.carenest.domain.usecase.settings.UpdateOnboardingStatusUseCase
import com.carenest.domain.repository.HomeRepository
import com.carenest.domain.usecase.home.GetServicesUseCase
import com.carenest.domain.usecase.home.GetUpcomingBookingUseCase
import com.carenest.domain.usecase.home.GetUserUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    @Provides
    @Singleton
    fun provideGetOnboardingStatusUseCase(
        settingsRepository: SettingsRepository
    ): GetOnboardingStatusUseCase {
        return GetOnboardingStatusUseCase(settingsRepository)
    }

    @Provides
    @Singleton
    fun provideUpdateOnboardingStatusUseCase(
        settingsRepository: SettingsRepository
    ): UpdateOnboardingStatusUseCase {
        return UpdateOnboardingStatusUseCase(settingsRepository)
    }

    @Provides
    @Singleton
    fun provideLoginWithPhoneUseCase(
        authRepository: AuthRepository
    ): LoginWithPhoneUseCase {
        return LoginWithPhoneUseCase(authRepository)
    }

    @Provides
    @Singleton
    fun provideRequestDevOtpUseCase(
        authRepository: AuthRepository
    ): RequestDevOtpUseCase {
        return RequestDevOtpUseCase(authRepository)
    }

    @Provides
    @Singleton
    fun provideVerifyOtpUseCase(
        authRepository: AuthRepository
    ): VerifyOtpUseCase {
        return VerifyOtpUseCase(authRepository)
    }

    @Provides
    @Singleton
    fun provideGetUserUseCase(
        homeRepository: HomeRepository
    ): GetUserUseCase {
        return GetUserUseCase(homeRepository)
    }

    @Provides
    @Singleton
    fun provideGetServicesUseCase(
        homeRepository: HomeRepository
    ): GetServicesUseCase {
        return GetServicesUseCase(homeRepository)
    }

    @Provides
    @Singleton
    fun provideGetUpcomingBookingUseCase(
        homeRepository: HomeRepository
    ): GetUpcomingBookingUseCase {
        return GetUpcomingBookingUseCase(homeRepository)
    }
}

