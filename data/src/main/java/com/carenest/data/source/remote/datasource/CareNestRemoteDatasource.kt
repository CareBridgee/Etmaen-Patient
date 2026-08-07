package com.carenest.data.source.remote.datasource

import com.carenest.data.source.remote.dto.ServiceDto
import com.carenest.data.source.remote.dto.user.UserResponseDto
import com.carenest.data.source.remote.dto.CreateServiceRequestDto
import com.carenest.data.source.remote.dto.ServiceRequestResponseDto
import com.carenest.domain.model.home.Booking

import com.carenest.data.source.remote.dto.history.ServiceHistoryDto

interface CareNestRemoteDatasource {
    suspend fun getServices(): Result<List<ServiceDto>>
    suspend fun getServiceDetails(serviceId: String) : Result<ServiceDto>
    suspend fun getUser() : Result<UserResponseDto>
    suspend fun getUserRequestsHistory() : Result<List<Booking>>
    suspend fun submitServiceRequest(body: CreateServiceRequestDto): Result<ServiceRequestResponseDto>
}
