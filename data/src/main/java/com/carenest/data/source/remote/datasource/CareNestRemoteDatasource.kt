package com.carenest.data.source.remote.datasource

import com.carenest.data.source.remote.dto.ServiceDto
import com.carenest.data.source.remote.dto.UserDto
import com.carenest.data.source.remote.dto.CreateServiceRequestDto
import com.carenest.data.source.remote.dto.ServiceRequestResponseDto
import com.carenest.domain.model.home.Booking


interface CareNestRemoteDatasource {
    suspend fun getServices(): Result<List<ServiceDto>>
    suspend fun getServiceDetails(serviceId: String) : Result<ServiceDto>
    suspend fun getUser() : Result<UserDto>
    suspend fun getUserRequestsHistory() : Result<List<Booking>>
    suspend fun submitServiceRequest(body: CreateServiceRequestDto): Result<ServiceRequestResponseDto>
}