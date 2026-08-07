package com.carenest.data.source.remote.datasource

import com.carenest.data.source.remote.dto.ServiceDto
import com.carenest.data.source.remote.dto.history.ServiceHistoryDto
import com.carenest.data.source.remote.service.CareNestApiService
import javax.inject.Inject


class CareNestRemoteDataSourceImpl @Inject constructor(
    private val careNestApiService: CareNestApiService
): CareNestRemoteDatasource {
    override suspend fun getServices(): Result<List<ServiceDto>> {
        return careNestApiService.getServices()
    }

    override suspend fun getServiceDetails(serviceId: String): Result<ServiceDto> {
        return careNestApiService.getServiceDetails(serviceId)
    }

    override suspend fun getUserRequestsHistory(): Result<List<ServiceHistoryDto>> {
        return careNestApiService.getServiceHistory()
    }
}
