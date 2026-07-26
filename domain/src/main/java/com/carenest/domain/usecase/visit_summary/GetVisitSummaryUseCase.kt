package com.carenest.domain.usecase.visit_summary

import com.carenest.domain.model.visit_summary.VisitSummary
import com.carenest.domain.repository.VisitSummaryRepository
import javax.inject.Inject

class GetVisitSummaryUseCase @Inject constructor(
    private val repository: VisitSummaryRepository,
) {
    suspend operator fun invoke(requestId: String): Result<VisitSummary> =
        repository.getVisitSummary(requestId)
}