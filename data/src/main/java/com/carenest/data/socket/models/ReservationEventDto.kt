package com.carenest.data.socket.models

import com.carenest.domain.socket.model.NurseOfferResponse
import com.carenest.domain.socket.model.ReservationEvent
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.Json

@Serializable
data class ReservationEventDto(
    val type: ReservationEventType,
    val reservationId: String,
    val data: JsonElement? = null
) {
    fun toDomain(json: Json): ReservationEvent {
        return when (type) {
            ReservationEventType.OFFER_CREATED -> ReservationEvent.OfferCreated(
                reservationId,
                json.decodeFromJsonElement<NurseOfferResponseDto>(data!!).toDomain()
            )
            ReservationEventType.OFFER_UPDATED -> ReservationEvent.OfferUpdated(
                reservationId,
                json.decodeFromJsonElement<NurseOfferResponseDto>(data!!).toDomain()
            )
            ReservationEventType.OFFER_COUNTERED -> ReservationEvent.OfferCountered(
                reservationId,
                json.decodeFromJsonElement<NurseOfferResponseDto>(data!!).toDomain()
            )
            ReservationEventType.OFFER_ACCEPTED -> ReservationEvent.OfferAccepted(
                reservationId,
                json.decodeFromJsonElement<NurseOfferResponseDto>(data!!).toDomain()
            )
            ReservationEventType.OFFER_WITHDRAWN -> ReservationEvent.OfferWithdrawn(
                reservationId,
                json.decodeFromJsonElement<OfferIdPayloadDto>(data!!).offerId
            )
            ReservationEventType.OFFER_REJECTED -> ReservationEvent.OfferRejected(
                reservationId,
                json.decodeFromJsonElement<OfferIdPayloadDto>(data!!).offerId
            )
            ReservationEventType.REQUEST_CANCELLED -> ReservationEvent.RequestCancelled(reservationId)
            ReservationEventType.COMPLETED -> ReservationEvent.Completed
            ReservationEventType.OFFERS_LIST -> ReservationEvent.OffersList(
                reservationId,
                json.decodeFromJsonElement<List<NurseOfferResponseDto>>(data!!).map { it.toDomain() }
            )
            ReservationEventType.UNKNOWN -> ReservationEvent.Unknown(reservationId, "UNKNOWN")
        }
    }
}

@Serializable
data class NurseInfoDto(
    val id: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val ratingAvg: Double? = null,
    val totalReviews: Int? = null
) {
    fun toDomain() = com.carenest.domain.socket.model.NurseInfo(
        id = id ?: "",
        firstName = firstName ?: "",
        lastName = lastName ?: "",
        ratingAvg = ratingAvg ?: 0.0,
        totalReviews = totalReviews ?: 0
    )
}

@Serializable
data class NurseOfferResponseDto(
    val id: String? = null,
    val serviceRequestId: String? = null,
    val nurse: NurseInfoDto? = null,
    val nurseId: String? = null,
    val proposedPrice: Double? = null,
    val proposedDate: String? = null,
    val proposedTime: String? = null,
    val message: String? = null,
    val status: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
) {
    fun toDomain() = NurseOfferResponse(
        id = id ?: "",
        serviceRequestId = serviceRequestId ?: "",
        nurse = nurse?.toDomain(),
        proposedPrice = proposedPrice ?: 0.0,
        proposedDate = proposedDate ?: "",
        proposedTime = proposedTime ?: "",
        message = message,
        status = status ?: "PENDING",
        createdAt = createdAt ?: "",
        updatedAt = updatedAt ?: "",
        nurseId = nurse?.id ?: nurseId ?: ""
    )
}

@Serializable
data class OfferIdPayloadDto(
    val offerId: String
)
