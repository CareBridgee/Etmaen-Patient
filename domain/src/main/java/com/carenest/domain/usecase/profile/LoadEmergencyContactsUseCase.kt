package com.carenest.domain.usecase.profile

import com.carenest.domain.model.profile.EmergencyContact
import com.carenest.domain.repository.ProfileRepository
import javax.inject.Inject

class LoadEmergencyContactsUseCase @Inject constructor(private val repository: ProfileRepository) {
    suspend operator fun invoke(profileId: String): Result<List<EmergencyContact>> =
        repository.getEmergencyContacts(profileId)
}
