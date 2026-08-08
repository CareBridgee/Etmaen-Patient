package com.carenest.data.source.remote.datasource

import com.carenest.data.source.remote.dto.tracking.NurseOfferDto
import com.carenest.data.source.remote.dto.tracking.VisitCodeResponseDto

interface NurseTrackingDataSource {

    suspend fun fetchNurseTrackingInfo(offerId: String): NurseOfferDto

    suspend fun cancelVisit(requestId: String): Boolean

    suspend fun fetchVerificationCode(requestId: String): VisitCodeResponseDto
}
