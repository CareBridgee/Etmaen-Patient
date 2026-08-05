package com.carenest.domain.usecase.tracking

import com.carenest.domain.repository.NurseTrackingRepository
import javax.inject.Inject

class GetVisitVerificationCodeUseCase @Inject constructor(
    private val repository: NurseTrackingRepository,
) {
    suspend operator fun invoke(requestId: String): Result<String> =
        repository.getVisitVerificationCode(requestId)
}
