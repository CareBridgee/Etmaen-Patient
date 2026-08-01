package com.carenest.domain.socket.model

sealed class ReservationEvent {
    abstract val reservationId: String

    data class OfferCreated(override val reservationId: String, val offer: NurseOfferResponse) : ReservationEvent()
    data class OfferUpdated(override val reservationId: String, val offer: NurseOfferResponse) : ReservationEvent()
    data class OfferCountered(override val reservationId: String, val offer: NurseOfferResponse) : ReservationEvent()
    data class OfferAccepted(override val reservationId: String, val offer: NurseOfferResponse) : ReservationEvent()
    data class OfferWithdrawn(override val reservationId: String, val offerId: String) : ReservationEvent()
    data class OfferRejected(override val reservationId: String, val offerId: String) : ReservationEvent()
    data class RequestCancelled(override val reservationId: String) : ReservationEvent()
    data class OffersList(override val reservationId: String, val offers: List<NurseOfferResponse>) : ReservationEvent()
    data class Unknown(override val reservationId: String, val type: String) : ReservationEvent()
}

data class NurseOfferResponse(
    val id: String,
    val serviceRequestId: String,
    val nurseId: String,
    val proposedPrice: Double,
    val proposedDate: String,
    val proposedTime: String,
    val message: String?,
    val status: String,
    val createdAt: String,
    val updatedAt: String
)

data class OfferIdPayload(
    val offerId: String
)
