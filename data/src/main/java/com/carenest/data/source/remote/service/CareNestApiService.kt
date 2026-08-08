package com.carenest.data.source.remote.service

import com.carenest.data.source.remote.dto.ServiceDto
import com.carenest.data.source.remote.dto.history.ReviewRequestDto
import com.carenest.data.source.remote.dto.history.ServiceHistoryDto
import com.carenest.data.source.remote.dto.history.VisitSummaryResponseDto
import com.carenest.data.source.remote.dto.CreateServiceRequestDto
import com.carenest.data.source.remote.dto.ServiceRequestResponseDto

interface CareNestApiService {
     suspend fun getServices(): Result<List<ServiceDto>>
     suspend fun getServiceDetails(serviceId: String): Result<ServiceDto>
     suspend fun getServiceHistory(): Result<List<ServiceHistoryDto>>
     suspend fun getServiceRequestDetails(requestId: String): Result<VisitSummaryResponseDto>
     suspend fun submitReview(review: ReviewRequestDto): Result<Unit>
     suspend fun submitServiceRequest(body: CreateServiceRequestDto): Result<ServiceRequestResponseDto>
}