package com.carenest.data.source.remote.datasource

import com.carenest.data.source.remote.dto.history.ReviewRequestDto
import com.carenest.data.source.remote.service.CareNestApiService
import com.carenest.domain.model.visit_summary.VisitSummary
import kotlinx.coroutines.delay
import javax.inject.Inject

class VisitSummaryDataSourceImp @Inject constructor(
    private val apiService: CareNestApiService
) : VisitSummaryDataSource {

    override suspend fun fetchVisitSummary(requestId: String): VisitSummary {
        delay(500)
        return VisitSummary(
            requestId = requestId,
            professionalName = "Sarah Mitchell",
            serviceType = "Wound Care",
            durationMinutes = 60,
            completedDate = "Oct 24",
            totalAmount = 85.00,
            isVerified = true,
        )
    }

    override suspend fun submitRating(requestId: String, rating: Int, comment: String?, isAnonymous: Boolean) {
        val review = ReviewRequestDto(
            bookingId = requestId,
            rating = rating,
            reviewText = comment.orEmpty(),
            isAnonymous = isAnonymous
        )
        apiService.submitReview(review).getOrThrow()
    }
}