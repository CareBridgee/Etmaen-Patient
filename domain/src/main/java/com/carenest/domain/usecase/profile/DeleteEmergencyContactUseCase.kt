package com.carenest.domain.usecase.profile

import com.carenest.domain.repository.ProfileRepository
import javax.inject.Inject

class DeleteEmergencyContactUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(emergencyContactId: String): Result<Unit> =
        repository.deleteEmergencyContact(emergencyContactId)
}
