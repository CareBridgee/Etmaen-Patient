package com.carenest.domain.di

import com.carenest.domain.repository.SettingsRepository
import com.carenest.domain.usecase.GetOnboardingStatusUseCase
import com.carenest.domain.usecase.UpdateOnboardingStatusUseCase
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
}
