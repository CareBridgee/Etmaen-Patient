package com.carenest.data.source.remote.datasource

import com.carenest.data.source.remote.dto.tracking.NurseDetailsDto
import com.carenest.data.source.remote.dto.tracking.ServiceRequestTrackingDto
import com.carenest.data.source.remote.dto.tracking.VisitCodeResponseDto
import com.carenest.data.source.remote.service.NurseTrackingService
import javax.inject.Inject

class NurseTrackingDataSourceImp @Inject constructor(
    private val nurseTrackingService: NurseTrackingService
) : NurseTrackingDataSource {

    override suspend fun fetchNurseTrackingInfo(requestId: String): ServiceRequestTrackingDto {
        return nurseTrackingService.fetchServiceRequest(requestId).getOrThrow()
    }

    override suspend fun fetchCurrentServiceRequest(): ServiceRequestTrackingDto {
        return nurseTrackingService.fetchCurrentServiceRequest().getOrThrow()
    }

    override suspend fun cancelVisit(requestId: String): Boolean {
        return nurseTrackingService.cancelVisit(requestId)
    }

    override suspend fun fetchVerificationCode(requestId: String): VisitCodeResponseDto {
        return nurseTrackingService.fetchVisitCode(requestId).getOrThrow()
    }

    override suspend fun fetchNurseDetails(nurseId: String): NurseDetailsDto {
        return nurseTrackingService.fetchNurseDetails(nurseId).getOrThrow()
    }
}