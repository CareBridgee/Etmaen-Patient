package com.carenest.domain.usecase.visitsummary

import com.carenest.domain.model.VisitSummary
import com.carenest.domain.repository.VisitSummaryRepository
import javax.inject.Inject

class GetVisitSummaryUseCase @Inject constructor(
    private val repository: VisitSummaryRepository,
) {
    suspend operator fun invoke(requestId: String): Result<VisitSummary> =
        repository.getVisitSummary(requestId)
}