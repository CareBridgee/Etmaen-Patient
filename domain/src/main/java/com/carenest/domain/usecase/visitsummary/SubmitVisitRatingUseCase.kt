package com.carenest.domain.usecase.visitsummary

import com.carenest.domain.repository.VisitSummaryRepository
import javax.inject.Inject

class SubmitVisitRatingUseCase @Inject constructor(
    private val repository: VisitSummaryRepository,
) {
    suspend operator fun invoke(requestId: String, rating: Int, comment: String? = null, isAnonymous: Boolean = false): Result<Unit> =
        repository.submitRating(requestId, rating, comment, isAnonymous)
}