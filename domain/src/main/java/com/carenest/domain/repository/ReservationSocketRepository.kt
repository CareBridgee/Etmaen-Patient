package com.carenest.domain.repository

import com.carenest.domain.socket.model.OfferCounterRequest
import com.carenest.domain.socket.model.OfferCreateRequest
import com.carenest.domain.socket.model.OfferUpdateRequest
import com.carenest.domain.socket.model.ReservationEvent
import kotlinx.coroutines.flow.Flow

interface ReservationSocketRepository {
    fun observeReservationEvents(reservationId: String): Flow<ReservationEvent>

    suspend fun createOffer(request: OfferCreateRequest)
    suspend fun updateOffer(request: OfferUpdateRequest)
    suspend fun counterOffer(request: OfferCounterRequest)
    suspend fun acceptOffer(offerId: String)
    suspend fun withdrawOffer(offerId: String)
    suspend fun rejectOffer(offerId: String)
    suspend fun cancelRequest(serviceRequestId: String)
    suspend fun requestOffersList(serviceRequestId: String)
}
