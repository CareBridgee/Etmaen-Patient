package com.carenest.domain.usecase.tracking

import com.carenest.domain.model.tracking.NurseTrackingInfo
import com.carenest.domain.repository.NurseTrackingRepository
import javax.inject.Inject

class GetCurrentNurseTrackingInfoUseCase @Inject constructor(
    private val repository: NurseTrackingRepository,
) {
    suspend operator fun invoke(): Result<NurseTrackingInfo> =
        repository.getCurrentNurseTrackingInfo()
}
