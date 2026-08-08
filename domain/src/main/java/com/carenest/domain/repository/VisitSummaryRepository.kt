package com.carenest.domain.repository

import com.carenest.domain.model.visit_summary.VisitSummary


interface VisitSummaryRepository {
    suspend fun getVisitSummary(requestId: String): Result<VisitSummary>
    suspend fun submitRating(requestId: String, rating: Int, comment: String?, isAnonymous: Boolean): Result<Unit>
}