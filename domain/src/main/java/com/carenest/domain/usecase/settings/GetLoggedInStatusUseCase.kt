package com.carenest.domain.usecase.settings

import com.carenest.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLoggedInStatusUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(): Flow<Boolean> {
        return settingsRepository.getLoggedInStatus()
    }
}
