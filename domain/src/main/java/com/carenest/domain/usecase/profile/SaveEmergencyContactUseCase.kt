package com.carenest.domain.usecase.profile

import com.carenest.domain.model.profile.EmergencyContact
import com.carenest.domain.model.profile.EmergencyRelationship
import com.carenest.domain.repository.ProfileRepository
import com.carenest.domain.validation.ProfileValidator
import javax.inject.Inject

class SaveEmergencyContactUseCase @Inject constructor(private val repository: ProfileRepository) {
    suspend operator fun invoke(
        profileId: String,
        emergencyContactId: String?,
        contactName: String,
        relationship: EmergencyRelationship?,
        phoneNumber: String
    ): Result<EmergencyContact> {
        val input = runCatching {
            ProfileValidator.emergencyContact(contactName, relationship, phoneNumber)
        }.getOrElse { return Result.failure(it) }
        return if (emergencyContactId == null) {
            repository.createEmergencyContact(profileId, input)
        } else {
            repository.updateEmergencyContact(emergencyContactId, input)
        }
    }
}
