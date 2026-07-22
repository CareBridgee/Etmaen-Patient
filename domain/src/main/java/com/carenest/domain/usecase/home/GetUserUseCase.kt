package com.carenest.domain.usecase.home

import com.carenest.domain.model.home.User
import com.carenest.domain.repository.HomeRepository
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {
    suspend operator fun invoke(): Result<User> {
        return homeRepository.getUser()
    }
}
