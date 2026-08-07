package com.carenest.data.source.remote.datasource

import com.carenest.data.source.remote.dto.ServiceDto
import com.carenest.data.source.remote.dto.history.ServiceHistoryDto

interface CareNestRemoteDatasource {
    suspend fun getServices(): Result<List<ServiceDto>>
    suspend fun getServiceDetails(serviceId: String) : Result<ServiceDto>
    suspend fun getUserRequestsHistory(): Result<List<ServiceHistoryDto>>
}
