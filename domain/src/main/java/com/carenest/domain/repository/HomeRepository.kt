package com.carenest.domain.repository

import com.carenest.domain.model.CreateServiceRequestParams
import com.carenest.domain.model.ServiceRequestResult
import com.carenest.domain.model.ServiceDetailsModel
import com.carenest.domain.model.home.HealthcareService
import com.carenest.domain.model.home.User
import com.carenest.domain.model.ServiceHistory

interface HomeRepository {
    suspend fun getUser(): Result<User>
    suspend fun getServices(): Result<List<HealthcareService>>
    suspend fun getServiceDetails(serviceId: String): Result<ServiceDetailsModel>
    suspend fun getServiceHistory(): Result<List<ServiceHistory>>
    suspend fun getServiceHistoryDetails(requestId: String): Result<ServiceHistory>
    suspend fun submitServiceRequest(params: CreateServiceRequestParams): Result<ServiceRequestResult>
}
