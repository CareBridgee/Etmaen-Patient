package com.carenest.data.source.remote.datasource

import com.carenest.data.source.remote.dto.tracking.NurseOfferDto
import com.carenest.data.source.remote.dto.tracking.VisitCodeResponseDto
import com.carenest.data.source.remote.service.NurseTrackingService
import javax.inject.Inject

class NurseTrackingDataSourceImp @Inject constructor(
    private val nurseTrackingService: NurseTrackingService
) : NurseTrackingDataSource {

    override suspend fun fetchNurseTrackingInfo(offerId: String): NurseOfferDto {
        return nurseTrackingService.fetchNurseOffer(offerId).getOrThrow()
    }

    override suspend fun cancelVisit(requestId: String): Boolean {
        return nurseTrackingService.cancelVisit(requestId)
    }

    override suspend fun fetchVerificationCode(requestId: String): VisitCodeResponseDto {
        return nurseTrackingService.fetchVisitCode(requestId).getOrThrow()
    }
}