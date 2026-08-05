package com.carenest.data.repository

import com.carenest.data.mapper.toDomain
import com.carenest.data.mapper.toServiceDetails
import com.carenest.data.source.remote.datasource.CareNestRemoteDatasource
import com.carenest.domain.model.ServiceDetailsModel
import com.carenest.domain.model.home.Booking
import com.carenest.domain.model.home.HealthcareService
import com.carenest.domain.model.home.User
import com.carenest.domain.repository.HomeRepository
import com.carenest.domain.repository.UserRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class HomeRepositoryImpl @Inject constructor(
    private val careNestRemoteDatasource: CareNestRemoteDatasource,
    private val userRepository: UserRepository
) : HomeRepository {

    override suspend fun getUser(): Result<User> {
        val cached = userRepository.observeCurrentUser().first()
        return cached?.let { Result.success(it) } ?: userRepository.refreshCurrentUser()
    }

    override suspend fun getServices(): Result<List<HealthcareService>> {
        return careNestRemoteDatasource.getServices().map { it -> it.map { it.toDomain() } }
    }

    override suspend fun getServiceDetails(serviceId: String): Result<ServiceDetailsModel> {
        return careNestRemoteDatasource.getServiceDetails(serviceId).map { it.toServiceDetails() }
    }

    override suspend fun getUserRequestsHistory(): Result<List<Booking>> {
        return careNestRemoteDatasource.getUserRequestsHistory()
    }
}
