package com.carenest.data.repository

import com.carenest.data.mapper.history.toDomain
import com.carenest.data.mapper.history.toEntity
import com.carenest.data.mapper.toDomain
import com.carenest.data.mapper.toServiceDetails
import com.carenest.data.source.local.database.dao.ServiceHistoryDao
import com.carenest.data.source.remote.datasource.CareNestRemoteDatasource
import com.carenest.data.source.remote.dto.CreateServiceRequestDto
import com.carenest.domain.model.CreateServiceRequestParams
import com.carenest.domain.model.ServiceDetailsModel
import com.carenest.domain.model.ServiceRequestResult
import com.carenest.domain.model.ServiceHistory
import com.carenest.domain.model.home.HealthcareService
import com.carenest.domain.model.home.User
import com.carenest.domain.repository.HomeRepository
import com.carenest.domain.repository.UserRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

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
        return careNestRemoteDatasource.getServices().map { it -> it.map { it.toDomain() }.shuffled() }
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
    override suspend fun submitServiceRequest(params: CreateServiceRequestParams): Result<ServiceRequestResult> {
        val dto = CreateServiceRequestDto(
            profileId = params.profileId,
            serviceTypeId = params.serviceTypeId,
            latitude = params.latitude,
            longitude = params.longitude,
            address = params.address,
            district = params.district,
            apartment = params.apartment,
            preferredDate = params.preferredDate,
            preferredTime = String.format(
                java.util.Locale.US,
                "%02d:%02d:%02d",
                params.preferredTime.hour,
                params.preferredTime.minute,
                params.preferredTime.second
            ),
            serviceDescription = params.serviceDescription,
            paymentType = params.paymentType.backendValue,
        )
        return careNestRemoteDatasource.submitServiceRequest(dto).map { response ->
            ServiceRequestResult(
                serviceRequestId = response.serviceRequestId,
                status = response.status,
                nearbyNursesCount = response.nearbyNurses.size,
            )
        }
    }
}
