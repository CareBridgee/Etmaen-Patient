package com.carenest.domain.usecase.settings

import com.carenest.domain.repository.SettingsRepository

class UpdateOnboardingStatusUseCase(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(status: Boolean) {
        settingsRepository.updateOnboardingStatus(status)
    }
}
