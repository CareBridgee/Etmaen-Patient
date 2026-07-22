package com.carenest.domain.usecase.home

import com.carenest.domain.model.home.HealthcareService
import com.carenest.domain.repository.HomeRepository
import javax.inject.Inject

class GetServicesUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {
    suspend operator fun invoke(): Result<List<HealthcareService>> {
        return homeRepository.getServices()
    }
}
