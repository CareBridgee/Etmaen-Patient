package com.carenest.data.source.remote.datasource

import com.carenest.data.source.remote.dto.history.ReviewRequestDto
import com.carenest.data.source.remote.dto.history.VisitSummaryResponseDto
import com.carenest.data.source.remote.service.CareNestApiService
import javax.inject.Inject

class VisitSummaryDataSourceImp @Inject constructor(
    private val apiService: CareNestApiService
) : VisitSummaryDataSource {

    override suspend fun fetchVisitSummary(requestId: String): VisitSummaryResponseDto {
        return apiService.getServiceRequestDetails(requestId).getOrThrow()
    }

    override suspend fun submitRating(requestId: String, rating: Int, comment: String?, isAnonymous: Boolean) {
        val review = ReviewRequestDto(
            serviceRequestId = requestId,
            rating = rating,
            reviewText = comment.orEmpty(),
            isAnonymous = isAnonymous
        )
        apiService.submitReview(review).getOrThrow()
    }
}