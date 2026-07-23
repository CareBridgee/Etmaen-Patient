package com.carenest.domain.usecase.profile

import com.carenest.domain.model.profile.EmergencyContact
import com.carenest.domain.model.profile.EmergencyContactInput
import com.carenest.domain.repository.ProfileRepository
import javax.inject.Inject

class SaveEmergencyContactUseCase @Inject constructor(private val repository: ProfileRepository) {
    suspend operator fun invoke(
        profileId: String,
        emergencyContactId: String?,
        input: EmergencyContactInput
    ): Result<EmergencyContact> = if (emergencyContactId == null) {
        repository.createEmergencyContact(profileId, input)
    } else {
        repository.updateEmergencyContact(emergencyContactId, input)
    }
}
