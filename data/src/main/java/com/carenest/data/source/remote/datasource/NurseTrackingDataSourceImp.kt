package com.carenest.data.source.remote.datasource

import android.util.Log
import com.carenest.data.source.remote.service.NurseTrackingService
import com.carenest.domain.model.tracking.NurseTrackingInfo
import kotlinx.coroutines.delay
import javax.inject.Inject

class NurseTrackingDataSourceImp @Inject constructor(
    private val nurseTrackingService: NurseTrackingService
): NurseTrackingDataSource{

    override suspend fun fetchNurseTrackingInfo(requestId: String): NurseTrackingInfo {
        // Keeping mock for now or you want me to connect all? 
        // The user specifically asked for the qr code endpoint.
        delay(600) // simulate network latency
        return NurseTrackingInfo(
            nurseId = "nurse_001",
            name = "Mark Harrison",
            photoUrl = null,
            rating = 4.9,
            reviewsCount = 210,
            estimatedArrivalTime = "10:30 AM",
            distanceKm = 2.4,
            specialty = "Geriatric",
            phoneNumber = "+15551234567",
            cancellationWindowMinutes = 2,
            requestId = requestId,
        )
    }
    override suspend fun cancelVisit(requestId: String): Boolean {
        delay(400) // simulate network latency
        // Mock: always within the free-cancellation window.
        return true
    }

    override suspend fun fetchVerificationCode(requestId: String): String {
        Log.e("NurseTrackingDataSourceImp", "fetchVerificationCode: ${nurseTrackingService.fetchVisitCode(requestId).getOrThrow().code}")
        return nurseTrackingService.fetchVisitCode(requestId).getOrThrow().code
    }
}