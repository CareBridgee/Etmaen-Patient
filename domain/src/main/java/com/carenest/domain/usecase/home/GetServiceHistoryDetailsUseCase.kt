package com.carenest.domain.usecase.home

import com.carenest.domain.model.history.ServiceHistory
import com.carenest.domain.repository.HomeRepository
import javax.inject.Inject

class GetServiceHistoryDetailsUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {
    suspend operator fun invoke(requestId: String): Result<ServiceHistory> {
        return homeRepository.getServiceHistoryDetails(requestId)
    }
}