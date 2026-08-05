package com.carenest.domain.usecase.profile

import com.carenest.domain.model.profile.EmergencyContact
import com.carenest.domain.repository.ProfileRepository
import javax.inject.Inject

class GetEmergencyContactByIdUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(emergencyContactId: String): Result<EmergencyContact> =
        repository.getEmergencyContactById(emergencyContactId)
}
