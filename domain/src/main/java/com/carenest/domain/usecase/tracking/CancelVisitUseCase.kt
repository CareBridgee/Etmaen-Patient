package com.carenest.domain.usecase.tracking

import com.carenest.domain.repository.NurseTrackingRepository
import javax.inject.Inject

class CancelVisitUseCase @Inject constructor(
    private val repository: NurseTrackingRepository,
) {
    suspend operator fun invoke(requestId: String): Result<Boolean> =
        repository.cancelVisit(requestId)
}
