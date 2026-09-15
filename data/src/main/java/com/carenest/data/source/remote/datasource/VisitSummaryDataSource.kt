package com.carenest.data.source.remote.datasource

import com.carenest.data.source.remote.dto.history.VisitSummaryResponseDto

interface VisitSummaryDataSource {
    suspend fun fetchVisitSummary(requestId: String): VisitSummaryResponseDto
    suspend fun submitRating(requestId: String, rating: Int, comment: String?, isAnonymous: Boolean)
}