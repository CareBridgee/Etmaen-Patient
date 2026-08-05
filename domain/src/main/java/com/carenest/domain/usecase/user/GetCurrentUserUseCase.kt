package com.carenest.domain.usecase.user

import com.carenest.domain.model.home.User
import com.carenest.domain.repository.UserRepository
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(): Result<User> = repository.refreshCurrentUser()
}
