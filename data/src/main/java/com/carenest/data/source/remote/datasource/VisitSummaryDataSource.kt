package com.carenest.data.source.remote.datasource

import com.carenest.domain.model.visit_summary.VisitSummary

interface VisitSummaryDataSource {
    suspend fun fetchVisitSummary(requestId: String): VisitSummary
    suspend fun submitRating(requestId: String, rating: Int, comment: String?, isAnonymous: Boolean)
}