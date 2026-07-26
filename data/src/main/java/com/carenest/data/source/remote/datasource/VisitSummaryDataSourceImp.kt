package com.carenest.data.source.remote.datasource

import com.carenest.domain.model.visit_summary.VisitSummary
import kotlinx.coroutines.delay
import javax.inject.Inject

class VisitSummaryDataSourceImp @Inject constructor() : VisitSummaryDataSource {

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

    override suspend fun submitRating(requestId: String, rating: Int, comment: String?) {
        delay(300)
        // mock: accept and no-op
    }
}