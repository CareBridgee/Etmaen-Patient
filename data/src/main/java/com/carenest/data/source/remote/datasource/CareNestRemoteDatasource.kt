package com.carenest.data.source.remote.datasource

import com.carenest.data.source.remote.dto.ServiceDto
import com.carenest.domain.model.home.Booking


interface CareNestRemoteDatasource {
    suspend fun getServices(): Result<List<ServiceDto>>
    suspend fun getServiceDetails(serviceId: String) : Result<ServiceDto>
    suspend fun getUserRequestsHistory() : Result<List<Booking>>
}
