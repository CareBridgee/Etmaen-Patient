package com.carenest.domain.usecase.user

import com.carenest.domain.model.home.User
import com.carenest.domain.repository.UserRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveCurrentUserUseCase @Inject constructor(
    private val repository: UserRepository
) {
    operator fun invoke(): Flow<User?> = repository.observeCurrentUser()
}
