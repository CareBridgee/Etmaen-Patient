package com.carenest.data.source.remote.datasource

import com.carenest.domain.model.tracking.NurseTrackingInfo

interface NurseTrackingDataSource {

    suspend fun fetchNurseTrackingInfo(requestId: String): NurseTrackingInfo

    suspend fun cancelVisit(requestId: String): Boolean

    suspend fun fetchVerificationCode(requestId: String): String
}
