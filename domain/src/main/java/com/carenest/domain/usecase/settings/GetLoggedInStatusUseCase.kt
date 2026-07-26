package com.carenest.domain.usecase.settings

import com.carenest.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class GetLoggedInStatusUseCase(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(): Flow<Boolean> {
        return settingsRepository.getLoggedInStatus()
    }
}
