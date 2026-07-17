package com.carenest.domain.usecase

import com.carenest.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class GetOnboardingStatusUseCase(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(): Flow<Boolean> {
        return settingsRepository.getOnboardingStatus()
    }
}
