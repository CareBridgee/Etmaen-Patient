package com.carenest.data.source.remote.datasource

import com.carenest.data.source.remote.dto.history.VisitSummaryResponseDto
import com.carenest.domain.model.visit_summary.VisitSummary

interface VisitSummaryDataSource {
    suspend fun fetchVisitSummary(requestId: String): VisitSummaryResponseDto
    suspend fun submitRating(requestId: String, rating: Int, comment: String?, isAnonymous: Boolean)
}