package com.carenest.data.repository

import com.carenest.data.di.IoDispatcher
import com.carenest.data.mapper.toDomain
import com.carenest.data.mapper.toServiceDetails
import com.carenest.data.mapper.toUser
import com.carenest.data.source.remote.datasource.CareNestRemoteDatasource
import com.carenest.domain.model.ServiceDetailsModel
import com.carenest.domain.model.home.Booking
import com.carenest.domain.model.home.HealthcareService
import com.carenest.domain.model.home.User
import com.carenest.domain.repository.HomeRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val careNestRemoteDatasource: CareNestRemoteDatasource,
) : HomeRepository {

    override suspend fun getUser(): Result<User> {
        return careNestRemoteDatasource.getUser().map { it.toUser() }
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
