package com.carenest.data.repository

import com.carenest.domain.repository.ReservationSocketRepository
import com.carenest.data.socket.SocketManagerImpl
import com.carenest.domain.socket.model.CancelRequest
import com.carenest.domain.socket.model.OfferActionRequest
import com.carenest.domain.socket.model.OfferCounterRequest
import com.carenest.domain.socket.model.OfferCreateRequest
import com.carenest.domain.socket.model.OfferUpdateRequest
import com.carenest.domain.socket.model.OffersListRequest
import com.carenest.domain.socket.model.ReservationEvent
import com.carenest.data.socket.models.ReservationEventDto
import com.carenest.data.socket.models.toDto
import com.carenest.data.socket.serialization.MessageSerializer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

class ReservationSocketRepositoryImpl @Inject constructor(
    private val socketManager: SocketManagerImpl,
    private val messageSerializer: MessageSerializer
) : ReservationSocketRepository {

    override fun observeReservationEvents(reservationId: String): Flow<ReservationEvent> {
        return socketManager.subscribe("/topic/reservation/$reservationId")
            .mapNotNull { messageSerializer.decodeFromString<ReservationEventDto>(it)?.toDomain(messageSerializer.json) }
    }

    override suspend fun createOffer(request: OfferCreateRequest) {
        val payload = messageSerializer.encodeToString(request.toDto())
        socketManager.send("/app/reservation/offer/create", payload)
    }

    override suspend fun updateOffer(request: OfferUpdateRequest) {
        val payload = messageSerializer.encodeToString(request.toDto())
        socketManager.send("/app/reservation/offer/update", payload)
    }

    override suspend fun counterOffer(request: OfferCounterRequest) {
        val payload = messageSerializer.encodeToString(request.toDto())
        socketManager.send("/app/reservation/offer/counter", payload)
    }

    override suspend fun acceptOffer(offerId: String) {
        val payload = messageSerializer.encodeToString(OfferActionRequest(offerId).toDto())
        socketManager.send("/app/reservation/offer/accept", payload)
    }

    override suspend fun withdrawOffer(offerId: String) {
        val payload = messageSerializer.encodeToString(OfferActionRequest(offerId).toDto())
        socketManager.send("/app/reservation/offer/withdraw", payload)
    }

    override suspend fun rejectOffer(offerId: String) {
        val payload = messageSerializer.encodeToString(OfferActionRequest(offerId).toDto())
        socketManager.send("/app/reservation/offer/reject", payload)
    }

    override suspend fun cancelRequest(serviceRequestId: String) {
        val payload = messageSerializer.encodeToString(CancelRequest(serviceRequestId).toDto())
        socketManager.send("/app/reservation/cancel", payload)
    }

    override suspend fun requestOffersList(serviceRequestId: String) {
        val payload = messageSerializer.encodeToString(OffersListRequest(serviceRequestId).toDto())
        socketManager.send("/app/reservation/offers/list", payload)
    }
}
