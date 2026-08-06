package com.carenest.data.source.remote.service

import com.carenest.data.source.remote.dto.ServiceDto
import com.carenest.data.source.remote.dto.history.ServiceHistoryDto

interface CareNestApiService {
     suspend fun getServices(): Result<List<ServiceDto>>
     suspend fun getServiceDetails(serviceId: String): Result<ServiceDto>
     suspend fun getServiceHistory(): Result<List<ServiceHistoryDto>>
}
