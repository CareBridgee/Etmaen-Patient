package com.carenest.domain.usecase.tracking

import com.carenest.domain.model.NurseTrackingInfo
import com.carenest.domain.repository.NurseTrackingRepository
import javax.inject.Inject

class GetNurseTrackingInfoUseCase @Inject constructor(
    private val repository: NurseTrackingRepository,
) {
    suspend operator fun invoke(requestId: String): Result<NurseTrackingInfo> =
        repository.getNurseTrackingInfo(requestId)
}
