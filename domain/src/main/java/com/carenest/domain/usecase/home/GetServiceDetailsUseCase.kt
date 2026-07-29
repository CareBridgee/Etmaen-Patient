package com.carenest.domain.usecase.home

import com.carenest.domain.repository.HomeRepository
import javax.inject.Inject

class GetServiceDetailsUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {
    suspend operator fun invoke(serviceId: String) = homeRepository.getServiceDetails(serviceId)
}