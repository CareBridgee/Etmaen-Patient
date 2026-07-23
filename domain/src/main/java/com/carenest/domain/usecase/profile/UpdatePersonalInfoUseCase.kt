package com.carenest.domain.usecase.profile

import com.carenest.domain.model.profile.PersonalInfoUpdate
import com.carenest.domain.model.profile.Profile
import com.carenest.domain.repository.ProfileRepository
import javax.inject.Inject

class UpdatePersonalInfoUseCase @Inject constructor(private val repository: ProfileRepository) {
    suspend operator fun invoke(profileId: String, update: PersonalInfoUpdate): Result<Profile> =
        repository.updatePersonalInfo(profileId, update)
}
