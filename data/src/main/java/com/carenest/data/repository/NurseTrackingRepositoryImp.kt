package com.carenest.data.repository

import com.carenest.data.mapper.tracking.toDomain
import com.carenest.data.source.remote.datasource.NurseTrackingDataSource
import com.carenest.domain.model.tracking.NurseTrackingInfo
import com.carenest.domain.repository.NurseTrackingRepository
import javax.inject.Inject

class NurseTrackingRepositoryImpl @Inject constructor(
    private val dataSource: NurseTrackingDataSource,
) : NurseTrackingRepository {

    override suspend fun getNurseTrackingInfo(requestId: String): Result<NurseTrackingInfo> = runCatching {
        val request = dataSource.fetchNurseTrackingInfo(requestId)
        val nurseId = request.nurse?.id

        if (nurseId != null) {
            val nurseDetails = dataSource.fetchNurseDetails(nurseId)
            request.toDomain(nurseDetails)
        } else {
            request.toDomain(null)
        }
    }

    override suspend fun cancelVisit(requestId: String): Result<Boolean> =
        runCatching { dataSource.cancelVisit(requestId) }

    override suspend fun getVisitVerificationCode(requestId: String): Result<String> = runCatching {
        dataSource.fetchVerificationCode(requestId).code
    }
}
