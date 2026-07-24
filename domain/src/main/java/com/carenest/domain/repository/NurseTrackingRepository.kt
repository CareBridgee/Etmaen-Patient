package com.carenest.domain.repository

import com.carenest.domain.model.tracking.NurseTrackingInfo

interface NurseTrackingRepository {

    suspend fun getNurseTrackingInfo(requestId: String): Result<NurseTrackingInfo>
    suspend fun cancelVisit(requestId: String): Result<Boolean>
}
