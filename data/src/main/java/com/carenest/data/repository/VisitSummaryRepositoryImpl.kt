package com.carenest.data.repository

import com.carenest.data.mapper.history.toDomain
import com.carenest.data.source.remote.datasource.VisitSummaryDataSource
import com.carenest.domain.model.visit_summary.VisitSummary
import com.carenest.domain.repository.VisitSummaryRepository
import javax.inject.Inject

class VisitSummaryRepositoryImpl @Inject constructor(
    private val dataSource: VisitSummaryDataSource,
) : VisitSummaryRepository {

    override suspend fun getVisitSummary(requestId: String): Result<VisitSummary> =
        runCatching { dataSource.fetchVisitSummary(requestId).toDomain() }

    override suspend fun submitRating(requestId: String, rating: Int, comment: String?, isAnonymous: Boolean): Result<Unit> =
        runCatching { dataSource.submitRating(requestId, rating, comment, isAnonymous) }
}