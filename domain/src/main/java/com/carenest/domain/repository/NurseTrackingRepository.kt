package com.carenest.domain.repository

import com.carenest.domain.model.tracking.NurseTrackingInfo
import com.carenest.domain.socket.model.NurseOfferResponse

interface NurseTrackingRepository {

    suspend fun getNurseTrackingInfo(requestId: String): Result<NurseTrackingInfo>
    suspend fun getCurrentNurseTrackingInfo(): Result<NurseTrackingInfo>
    suspend fun cancelVisit(requestId: String): Result<Boolean>
    suspend fun getVisitVerificationCode(requestId: String): Result<String>
    suspend fun getNurseOffers(serviceRequestId: String): Result<List<NurseOfferResponse>>
}
