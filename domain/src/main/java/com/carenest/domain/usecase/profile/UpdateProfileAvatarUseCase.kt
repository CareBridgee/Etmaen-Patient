package com.carenest.domain.usecase.profile

import com.carenest.domain.model.home.User
import com.carenest.domain.model.user.UserUpdate
import com.carenest.domain.repository.UserRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class UpdateProfileAvatarUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(
        fileName: String,
        contentType: String,
        bytes: ByteArray
    ): Result<User> {
        if (bytes.isEmpty()) return Result.failure(IllegalArgumentException("Selected image is empty"))

        val currentUser = repository.observeCurrentUser().first()
            ?: repository.refreshCurrentUser().getOrElse { return Result.failure(it) }
        val firstName = currentUser.firstName?.takeIf(String::isNotBlank)
            ?: return Result.failure(IllegalStateException("Current user first name is unavailable"))
        val lastName = currentUser.lastName?.takeIf(String::isNotBlank)
            ?: return Result.failure(IllegalStateException("Current user last name is unavailable"))
        val imageUrl = repository.uploadProfileImage(fileName, contentType, bytes)
            .getOrElse { return Result.failure(it) }

        return repository.updateCurrentUser(
            UserUpdate(
                firstName = firstName,
                lastName = lastName,
                email = currentUser.email,
                dateOfBirth = currentUser.dateOfBirth,
                gender = currentUser.gender,
                profileImageUrl = imageUrl
            )
        )
    }
}
