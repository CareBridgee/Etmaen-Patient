package com.carenest.data.repository

import com.carenest.data.mapper.history.toDomain
import com.carenest.data.mapper.history.toEntity
import com.carenest.data.mapper.toDomain
import com.carenest.data.mapper.toServiceDetails
import com.carenest.data.source.local.database.dao.ServiceHistoryDao
import com.carenest.data.source.remote.datasource.CareNestRemoteDatasource
import com.carenest.domain.model.ServiceDetailsModel
import com.carenest.domain.model.history.ServiceHistory
import com.carenest.domain.model.home.Booking
import com.carenest.domain.model.home.HealthcareService
import com.carenest.domain.model.home.User
import com.carenest.domain.repository.HomeRepository
import com.carenest.domain.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first

@Singleton
class HomeRepositoryImpl @Inject constructor(
    private val careNestRemoteDatasource: CareNestRemoteDatasource,
    private val userRepository: UserRepository,
    private val serviceHistoryDao: ServiceHistoryDao
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

    override suspend fun getServiceHistory(): Result<List<ServiceHistory>> {
        return careNestRemoteDatasource.getUserRequestsHistory().map { list ->
            val domainList = list.map { it.toDomain() }
            serviceHistoryDao.insertAll(domainList.map { it.toEntity() })
            domainList
        }
    }

    override suspend fun getServiceHistoryDetails(requestId: String): Result<ServiceHistory> {
        val entity = serviceHistoryDao.getHistoryById(requestId)
        return if (entity != null) {
            Result.success(entity.toDomain())
        } else {
            Result.failure(Exception("Service history details not found in database"))
        }
    }
}
