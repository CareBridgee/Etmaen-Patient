package com.carenest.domain.socket.model

sealed class ReservationEvent {
    abstract val reservationId: String

    data class OfferCreated(override val reservationId: String, val offer: NurseOfferResponse) : ReservationEvent()
    data class OfferUpdated(override val reservationId: String, val offer: NurseOfferResponse) : ReservationEvent()
    data class OfferCountered(override val reservationId: String, val offer: NurseOfferResponse) : ReservationEvent()
    data class OfferAccepted(override val reservationId: String, val offer: NurseOfferResponse) : ReservationEvent()
    data class OfferWithdrawn(override val reservationId: String, val offerId: String) : ReservationEvent()
    data class OfferRejected(override val reservationId: String, val offerId: String) : ReservationEvent()
    data object Completed : ReservationEvent() {
        override val reservationId: String = "" // Special case for COMPLETED which has null data
    }
    data class RequestCancelled(override val reservationId: String) : ReservationEvent()
    data class OffersList(override val reservationId: String, val offers: List<NurseOfferResponse>) : ReservationEvent()
    data class Unknown(override val reservationId: String, val type: String) : ReservationEvent()
}

data class NurseInfo(
    val id: String,
    val firstName: String,
    val lastName: String,
    val ratingAvg: Double,
    val totalReviews: Int
)

data class NurseOfferResponse(
    val id: String,
    val serviceRequestId: String,
    val nurse: NurseInfo? = null,
    val proposedPrice: Double,
    val proposedDate: String,
    val proposedTime: String,
    val message: String?,
    val status: String,
    val createdAt: String,
    val updatedAt: String,
    val nurseId: String = nurse?.id ?: ""
)

data class OfferIdPayload(
    val offerId: String
)
