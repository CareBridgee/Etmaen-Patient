package com.carenest.domain.usecase.home

import com.carenest.domain.model.history.ServiceHistory
import com.carenest.domain.repository.HomeRepository
import javax.inject.Inject

class GetUserRequestHistoryUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {
    suspend operator fun invoke(): Result<List<ServiceHistory>> {
        return homeRepository.getServiceHistory()
    }
}
