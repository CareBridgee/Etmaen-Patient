package com.carenest.domain.usecase.user

import com.carenest.domain.model.home.User
import com.carenest.domain.model.user.UserUpdate
import com.carenest.domain.repository.UserRepository
import com.carenest.domain.validation.ProfileValidator
import javax.inject.Inject

class UpdateCurrentUserUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(
        firstName: String,
        lastName: String,
        dateOfBirth: String,
        gender: String,
        email: String? = null,
        profileImageUrl: String? = null
    ): Result<User> {
        val personalInfo = runCatching {
            ProfileValidator.personalInfo(firstName, lastName, dateOfBirth, gender)
        }.getOrElse { return Result.failure(it) }

        return repository.updateCurrentUser(
            UserUpdate(
                firstName = personalInfo.firstName,
                lastName = personalInfo.lastName,
                email = email,
                dateOfBirth = personalInfo.dateOfBirth,
                gender = personalInfo.gender,
                profileImageUrl = profileImageUrl
            )
        )
    }
}
