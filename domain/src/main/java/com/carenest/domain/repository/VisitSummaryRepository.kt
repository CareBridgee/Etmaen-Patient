package com.carenest.domain.repository

import com.carenest.domain.model.VisitSummary


interface VisitSummaryRepository {
    suspend fun getVisitSummary(requestId: String): Result<VisitSummary>
    suspend fun submitRating(requestId: String, rating: Int, comment: String?, isAnonymous: Boolean): Result<Unit>
}