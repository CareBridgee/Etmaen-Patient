package com.carenest.data.source.remote.service

import com.carenest.data.source.remote.dto.tracking.VisitCodeResponseDto
import com.carenest.domain.model.tracking.NurseTrackingInfo

interface NurseTrackingService {
    suspend fun fetchNurseTrackingInfo(requestId: String): NurseTrackingInfo
    suspend fun cancelVisit(requestId: String): Boolean
    suspend fun fetchVisitCode(requestId: String): Result<VisitCodeResponseDto>
}