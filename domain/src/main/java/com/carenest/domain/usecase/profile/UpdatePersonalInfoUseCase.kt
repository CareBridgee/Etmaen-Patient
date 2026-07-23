package com.carenest.domain.usecase.profile

import com.carenest.domain.model.profile.Profile
import com.carenest.domain.repository.ProfileRepository
import com.carenest.domain.validation.ProfileValidator
import javax.inject.Inject

class UpdatePersonalInfoUseCase @Inject constructor(private val repository: ProfileRepository) {
    suspend operator fun invoke(
        profileId: String,
        firstName: String,
        lastName: String,
        dateOfBirth: String,
        gender: String
    ): Result<Profile> {
        val update = runCatching {
            ProfileValidator.personalInfo(firstName, lastName, dateOfBirth, gender)
        }.getOrElse { return Result.failure(it) }
        return repository.updatePersonalInfo(profileId, update)
    }
}
