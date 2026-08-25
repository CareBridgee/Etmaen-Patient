package com.carenest.domain.usecase.tracking

import com.carenest.domain.repository.NurseTrackingRepository
import com.carenest.domain.socket.model.NurseOfferResponse
import javax.inject.Inject

class GetNurseOffersUseCase @Inject constructor(
    private val repository: NurseTrackingRepository,
) {
    suspend operator fun invoke(serviceRequestId: String): Result<List<NurseOfferResponse>> =
        repository.getNurseOffers(serviceRequestId)
}
