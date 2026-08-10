package com.carenest.data.source.remote.datasource

import com.carenest.data.source.remote.dto.tracking.NurseDetailsDto
import com.carenest.data.source.remote.dto.tracking.ServiceRequestTrackingDto
import com.carenest.data.source.remote.dto.tracking.VisitCodeResponseDto

interface NurseTrackingDataSource {

    suspend fun fetchNurseTrackingInfo(requestId: String): ServiceRequestTrackingDto

    suspend fun fetchCurrentServiceRequest(): ServiceRequestTrackingDto

    suspend fun cancelVisit(requestId: String): Boolean

    suspend fun fetchVerificationCode(requestId: String): VisitCodeResponseDto

    suspend fun fetchNurseDetails(nurseId: String): NurseDetailsDto
}
